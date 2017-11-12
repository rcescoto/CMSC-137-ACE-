import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
	public static void main(String[] args){
		try {
			//scans the client's username (java -jar ChatClient.jar <clientName>)
			String clientName = args[0];
			
			//create a scanner object and int placeholder to get the server port
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter server port: ");
			int port = scanner.nextInt();
			
			//create a socket object
			Socket socket = new Socket("localhost", port);
			
			//create printwriter and bufferedreader objects to get the client's input
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				//chat proper
				System.out.print("Enter message: ");
				String readerInput = bufferedReader.readLine();
				printWriter.println("(" + clientName + ") " + readerInput);
				
				//close chat by typing /quit
				if (readerInput.equals("/quit")) {
					bufferedReader.close();
					printWriter.close();
					scanner.close();
					socket.close();
					System.exit(0);
				}
			}
		} catch (Exception e) {
			System.out.println("include your username in the command-line arguments.");
		}
		
		
	}
}
