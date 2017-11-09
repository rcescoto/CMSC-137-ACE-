import java.util.Scanner;
import java.net.*;
import java.io.*;
// guided by http://makemobiapps.blogspot.com/p/multiple-client-server-chat-programming.html?m=1
public class ChatClient implements Runnable {
	static Socket clientSocket = null;
	static DataOutputStream outStream = null;
	static DataInputStream inStream = null;
	static Scanner reader = new Scanner(System.in);
	static boolean offline = false;

	public static void main(String args[]) {
		int portNumber = 2222;
		String host = "localhost";

		//gets host (ip address) and port number
		try {
			System.out.print("Enter host: "); 
			host = reader.nextLine();

			System.out.print("Enter port number: ");
			portNumber = reader.nextInt();

			//creates a socket based on the given host and port number
			clientSocket = new Socket(host, portNumber);
			System.out.println("\nConnected to " + host + " on port " + portNumber + ".");

			//initiliazes input and output stream
			reader = new Scanner(new InputStreamReader(System.in));
			outStream = new DataOutputStream(clientSocket.getOutputStream()); //for sending data to server
			inStream = new DataInputStream(clientSocket.getInputStream()); //for receiving data from server
		} catch (UnknownHostException uhe) {
			System.out.println("Unknown host " + host);
		} catch (IOException ie) {
			System.out.println("I/O not found.");
		}

		if (clientSocket != null && outStream != null && inStream != null) {
			try {
				//creates a thread to read from server
				new Thread(new ChatClient()).start();

				//sends data read to server
				while (offline != true) {
					outStream.writeUTF(reader.nextLine().trim());
				} 

				//close socket, dataoutputstream, datainputstream
				outStream.close();
				inStream.close();
				clientSocket.close();
			} catch (IOException ie) {
				System.out.println("IOException + " + ie);
			}
		}
	}

	public void run() {
		String inputText;

		//keep on reading from socket and printing it til "\quit" is received from server
		try {
			while((inputText = inStream.readUTF()) != null) {
				System.out.println(inputText);
				if (inputText == "Goodbye.") {
					break;
				}
			}
			offline = true;
		} catch (IOException ie) {
			System.exit(0);
			/*System.out.println("mark");
			System.out.println(ie);*/
		}
	}
}

