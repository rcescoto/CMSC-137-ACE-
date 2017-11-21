import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient implements Runnable {
	static Socket clientSocket = null;
	static DataOutputStream sendToServer = null;
	static DataInputStream receiveFromServer = null;
	static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args){
		int port;
		
		try {
			System.out.print("Enter server port number: ");
			port = scanner.nextInt();
			
			clientSocket = new Socket("localhost", port);
			System.out.println("Connected to server.");
			
			scanner = new Scanner(new InputStreamReader(System.in));
			
			sendToServer = new DataOutputStream(clientSocket.getOutputStream());
			receiveFromServer = new DataInputStream(clientSocket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (clientSocket != null && sendToServer != null && receiveFromServer != null) {
			try {
				//creates a thread to read from server
				new Thread(new ChatClient()).start();

				//sends data read to server
				while (true) {
					sendToServer.writeUTF(scanner.nextLine().trim());
				}
			} catch (IOException ie) {
				System.out.println("IOException + " + ie);
			}
		}
	}
	
	public void run() {
		String inputText;
		
		try {
			while ((inputText = receiveFromServer.readUTF()) != null) {
				System.out.println(inputText);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
