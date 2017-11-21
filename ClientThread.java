import java.io.*;
import java.net.*;

public class ClientThread extends Thread{
	DataInputStream receiveFromClient = null;
	DataOutputStream sendToClient = null;
	
	Socket clientSocket = null;
	
	ClientThread[] clientThreads;
	
	int maxPlayers;
	
	public ClientThread(Socket clientSocket, ClientThread[] clientThreads) {
		this.clientSocket = clientSocket;
		this.clientThreads = clientThreads;
		maxPlayers = clientThreads.length;
	}
	
	public void run() {
		try {
			receiveFromClient = new DataInputStream(clientSocket.getInputStream());
			sendToClient = new DataOutputStream(clientSocket.getOutputStream());
			
			sendToClient.writeUTF("Enter your name: ");
			String playerName = receiveFromClient.readUTF().trim();
			
			sendToClient.writeUTF("Welcome, " + playerName + ".");
			
			while (true) {
				String message = receiveFromClient.readUTF();
				
				for (int i = 0; i < maxPlayers; i++) {
					if (clientThreads[i] != null) {
						clientThreads[i].sendToClient.writeUTF("( " + playerName + "): " + message);
					}
						
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.run();
	}
}
