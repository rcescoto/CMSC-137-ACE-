import java.util.Scanner;
import java.net.*;
import java.io.*;
// guided by http://makemobiapps.blogspot.com/p/multiple-client-server-chat-programming.html?m=1
public class ChatServer {
	static Scanner scanner = new Scanner(System.in);
	static ServerSocket serverSocket = null;
	static Socket clientSocket = null;

	static int maxClients = 8;
	static ClientThread[] threads = new ClientThread[maxClients];

	public static void main(String args[]) {
		String menu = "Choose port number options before starting server:\n   [1] Provide Port Number \n   [2] Use Default Port Number\n   [3] Exit\n\nChoice: ";
		System.out.print(menu);

		int choice = 0;
		int portNumber = 2222;

		//menu for getting port number
		while (choice != 3) {
			choice = scanner.nextInt();
			if (choice == 1) {
				System.out.print("Enter port number (use values greater than 1023): ");
				portNumber = scanner.nextInt();

				while (portNumber < 1024) {
					System.out.print("Enter port number (use values greater than 1023): ");
					portNumber = scanner.nextInt();
				}

				System.out.println("\n");
				System.out.println("Now using port number: " + portNumber);
				break;
			} else if (choice == 2) {

				System.out.println("");
				System.out.println("Now using port number: " + portNumber);
				break;
			} else if (choice == 3) {
				System.exit(0);
			} else {
				System.out.println("Invalid choice. Try again");
			}
		}

		//opens a server socket via the given port number
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException io) {
			System.out.println(io);
		}

		while(true) {

			try {
				clientSocket = serverSocket.accept();

				//create a client socket for each time a client is created and pass it to new client thread
				int i = 0;
				for(i = 0; i < maxClients; i++) {
					if (threads[i] == null) {
						threads[i] = new ClientThread(clientSocket, threads);
						(threads[i]).start();
						break;
					}
				}

				//inform the client that a maximum number of members have been reached and therefore client cannot be accomodated
				if (i == maxClients) {
					DataOutputStream outStream = new DataOutputStream(clientSocket.getOutputStream());
					outStream.writeUTF("Server is full. Try later.");
					outStream.close();
					clientSocket.close();
				}

			} catch (IOException io) {
				System.out.println(io);
			}
		}
	} 
}