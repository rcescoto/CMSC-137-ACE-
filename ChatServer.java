import java.io.*;
import java.net.*;

public class ChatServer {
	
	public static void main(String[] args) throws IOException {
		new ChatServer().runServer();
	}
	
	public void runServer() {
		try {
			//initialize server socket
			ServerSocket serverSocket = new ServerSocket(0); //the port is allocated dynamically
			serverSocket.setSoTimeout(300000);
			serverSocket.setReuseAddress(true);
			
			//get the assigned port by the system
			int port = serverSocket.getLocalPort();
			
			//identify the port used so the clients will know which port to use
			System.out.println("Listening on Port " + port + "...");
			
			//server socket now accepts connection from clients
			while(true) {
				Socket socket = serverSocket.accept();
				new ChatServerThread(socket).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
