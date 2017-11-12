import java.io.*;
import java.net.*;

public class ChatServerThread extends Thread {
	
	//create a socket object
	Socket socket;
	
	//initialize the socket object
	public ChatServerThread(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		try {
			//get the client's message
			String message = null;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while ((message = bufferedReader.readLine()) != null) {
				System.out.println("Client: " + message);
				
				if (message.equals("/quit")) {
					//close socket
					socket.close();
					System.exit(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.run();
	}
}
