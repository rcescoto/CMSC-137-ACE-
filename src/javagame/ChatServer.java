package javagame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer implements Runnable {
	private ServerSocket serverSocket;
	public Socket clientSocket;
	public int totalPlayers;
	public int portNumber;
	ClientThread[] clientThreads;
	
	public ChatServer(int totalPlayers, int portNumber) {
		this.totalPlayers = totalPlayers;
		this.portNumber = portNumber;
	}
	
	@Override
	public void run() {
		System.out.println("chat-server-run-checker");
		
		clientThreads = new ClientThread[totalPlayers];
		
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int port = serverSocket.getLocalPort();
		
		System.out.println("Chat server running at port " + port);
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				
				for (int i = 0; i < totalPlayers; i++) {
					if (clientThreads[i] == null) {
						clientThreads[i] = new ClientThread(clientSocket, clientThreads);
						(clientThreads[i]).start();
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class ClientThread extends Thread {
		DataInputStream receiveFromClient = null;
		DataOutputStream sendToClient = null;
		
		Socket clientSocket = null;
		
		ClientThread[] clientThreads;
		
		public ClientThread(Socket clientSocket, ClientThread[] clientThreads) {
			this.clientSocket = clientSocket;
			this.clientThreads = clientThreads;
		}
		
		public void run() {
			System.out.println("client-thread-run-checker");
			
			try {
				receiveFromClient = new DataInputStream(clientSocket.getInputStream());
				sendToClient = new DataOutputStream(clientSocket.getOutputStream());
				
				while (true) {
					String message = receiveFromClient.readUTF();
					
					for (int i = 0; i < totalPlayers; i++) {
						if (clientThreads[i] != null) {
							clientThreads[i].sendToClient.writeUTF(message);
						}
							
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.run();
		}
	}
}
