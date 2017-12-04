package javagame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ChatServerReader implements Runnable{
	Socket clientSocket = null;
	static DataOutputStream sendToServer = null;
	static DataInputStream receiveFromServer = null;

	public ChatServerReader(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	@Override
	public void run() {
		String inputText;
		
		try {
			receiveFromServer = new DataInputStream(clientSocket.getInputStream());
			
			while ((inputText = receiveFromServer.readUTF()) != null) {
				System.out.println(inputText);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
