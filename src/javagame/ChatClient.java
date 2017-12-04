package javagame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient implements Runnable {

	static Socket clientSocket = null;
	static DataOutputStream sendToServer = null;
	static DataInputStream receiveFromServer = null;
	static Scanner scanner = new Scanner(System.in);
	static String server;
	static String name;
	
	public ChatClient(String server, String name) {
		ChatClient.name = name;
		ChatClient.server = server;
	}
	
	//public static void main(String[] args) 
	public void run() {
		System.out.println("chat-client-run-checker");
		
		try {
			clientSocket = new Socket(server, 50000);
			
			scanner = new Scanner(new InputStreamReader(System.in));
			
			sendToServer = new DataOutputStream(clientSocket.getOutputStream());
			receiveFromServer = new DataInputStream(clientSocket.getInputStream());
			
			if (clientSocket != null && sendToServer != null && receiveFromServer != null) {
				try {
					//creates a thread to read from server
					new Thread(new ChatServerReader(clientSocket)).start();

					//sends data read to server
					while (true) {
						sendToServer.writeUTF(scanner.nextLine().trim());
					}
				} catch (IOException ie) {
					System.out.println("IOException + " + ie);
				}
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
