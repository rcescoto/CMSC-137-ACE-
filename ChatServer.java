import java.io.*;
import java.net.*;

public class ChatServer {
	private ServerSocket serverSocket;
	public Socket clientSocket;
	public static int maxPlayers = 4;
	
	ClientThread[] clientThreads = new ClientThread[maxPlayers];
	
	public static void main(String[] args) throws IOException{
		new ChatServer().runServer();
	}

	private void runServer() {
		
		try {
			//initialize server socket
			serverSocket = new ServerSocket(0);	//socket port allocated dynamically
			
			//capture assigned port by system
			int serverPort = serverSocket.getLocalPort();
			
			//identify the port so clients will know which port to use
			System.out.println("Listening on port " + serverPort + "...");
			
			//server socket now accepts clients (max 4 players)
			while (true) {
				clientSocket = serverSocket.accept();
				
				for (int i = 0; i < maxPlayers; i++) {
					if (clientThreads[i] == null) {
						clientThreads[i] = new ClientThread(clientSocket, clientThreads);
						(clientThreads[i]).start();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
