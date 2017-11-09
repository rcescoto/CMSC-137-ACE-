import java.util.Scanner;
import java.net.*;
import java.io.*;
// guided by http://makemobiapps.blogspot.com/p/multiple-client-server-chat-programming.html?m=1

class ClientThread extends Thread {
	DataInputStream inStream = null;
	DataOutputStream outStream = null;
	Socket clientSocket = null;//
	ClientThread[] threads; //a list of threads for the client
	int maxClients;

	public ClientThread(Socket clientSocket, ClientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClients = threads.length;
	}

	public void run() {
		int maxClients = this.maxClients;
		ClientThread[] threads = this.threads;

		try {

			//create datainputstream and dataoutputstream
			inStream = new DataInputStream(clientSocket.getInputStream());
			outStream = new DataOutputStream(clientSocket.getOutputStream());

			//gets the name of the client and welcomes him/her
			outStream.writeUTF("Enter your name: ");
			String name = inStream.readUTF().trim();

			outStream.writeUTF("\n\tWelcome " + name + " to the chatroom.\n\tEnter \\quit in order to leave.\n");

			//broadcasts to all clients (except the newly created one) that a new client has been added
			synchronized (this) {
				for(int i=0; i < maxClients; i++) {
					if (threads[i] != null && threads[i] != this) {
						threads[i].outStream.writeUTF("\n\t>> New user " + name + " has entered the chatroom.\n");
					} 
				}
			}

			//gets message from clients, breaks when the client enters the keyword 'quit'
			while(true) {
				String input = inStream.readUTF();
				if (input.startsWith("\\quit")) {
					break;
				} 

				//displays message from a client to all clients
				synchronized (this) {
					for(int i=0; i < maxClients; i++) {
						if (threads[i] != null) {
							threads[i].outStream.writeUTF("> " + name + ": " + input);
						}
					}
				}
			}

			//if a client types the keyword, tell all other clients that the client has left
			synchronized (this) {
				for(int i=0; i < maxClients; i++) {
					if (threads[i] != null && threads[i] != this) {
						threads[i].outStream.writeUTF("User " + name + " left the chatroom.");
					} 
				}
			}
			outStream.writeUTF("Goodbye.");

			//clear the threads index array for the client that left so that others can use it
			synchronized (this) {
				for(int i=0; i < maxClients; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}

			inStream.close();
			outStream.close();
			clientSocket.close();
		} catch (IOException io) {
			System.out.println(io);
		}
	}
}