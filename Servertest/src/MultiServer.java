import java.io.*;
import java.net.*;

class ChatThread implements Runnable {
	private Socket sock = null;
	public ChatThread(Socket sock) {
		this.sock = sock;
}
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			PrintWriter out = new PrintWriter(sock.getOutputStream());
			while(true) {
				String msg = in.readLine();
				System.out.println(msg);
				out.println("OK");
				out.flush();
				if (msg.equals("bye")) break;
		}
		sock.close();
		} catch (IOException ex) { ex.printStackTrace(); }
	}
}

public class MultiServer {
		public static void main(String[] args) {
			try {
				ServerSocket srvsock = new ServerSocket(9999);
				System.out.println("Server started ... \n");
				for (int i=0; i<5; i++) {
					Socket sock = srvsock.accept();
					System.out.println("IP: " + sock.getInetAddress() + ", port: " + sock.getPort());
					Thread chat = new Thread(new ChatThread(sock));
					chat.start();
				}
				System.out.println("\nServer stopped ...");
				srvsock.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

}