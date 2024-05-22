import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Webserver3 {
	public static void main(String[] args) {
		try {
			ServerSocket srvsock = new ServerSocket(80);
			System.out.println("Server started ... \n");
			while(true) {
				Socket sock = srvsock.accept();
				ServerThread3 thread = new ServerThread3(sock);
				thread.start();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

class ServerThread3 extends Thread {
	Socket sock;
	public ServerThread3(Socket sock) { this.sock = sock; }
	public void run() {
		try {
			HttpRequest req = new HttpRequest();
			String msg = req.receive(sock.getInputStream());
			System.out.println(msg);
			
			
			String file = req.getFile(msg); // msg로부터 /index.html 추출
			System.out.println(file);
			
			HttpResponse res = new HttpResponse();
			res.send(sock.getOutputStream(), file);
			sock.close();
		} catch (IOException ex) { ex.printStackTrace(); }
	}
}


class HttpRequest {
	public String receive(InputStream is) throws IOException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream(); 
		byte[] buf = new byte[1024];
		int cnt;
		while((cnt = is.read(buf)) != -1) {
			bao.write(buf);
			if (cnt < buf.length) break;
		};
		return bao.toString();
	}
	
	
	public String getFile(String msg) {
		String[] lines = msg.trim().split("\n");
		if (lines.length == 0) return null;
		
		System.out.println("first: " + lines[0]); // first: GET /index.html HTTP/1.1
		String[] toks = lines[0].trim().split(" ");
		if (toks.length < 2) return null;
		if (!toks[0].equals("GET") && !toks[0].equals("POST")) return null;
		return toks[1];
	}
}


class HttpResponse {
	public void send(OutputStream os, String file) throws IOException {
		file = "web" + file; // HTML 파일이 저장되는 루트 디렉토리로 web을 지정
		System.out.println("send file: " + file);
		String msg = "";
		if ((new File(file)).exists() == false) {
			msg = "<html><meta charset='utf-8'>요청하신 파일이 존재하지 않습니다.</html>";
		}
		else {
			byte[] bytes = Files.readAllBytes(Paths.get(file)); // 08.파일 입출력 PPT FileUtil 클래스 참고
			msg = new String(bytes, "utf-8");
		}
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "utf-8"));
		pw.println(msg);
		pw.flush();
	}
}