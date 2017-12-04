package javagame;

import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

//CLASS FOR THE GAME'S SERVER
public class GameServer implements Runnable {
    // possible server states
    public final int WAITING_PLAYER = 0;
    public final int GAME_START = 1;
    public final int IN_PROGRESS = 2;
    public final int GAME_END = 3;

    DatagramSocket serverSocket = null;
    public int portNumber = 5559;
    ServerState game;
    Thread thread = new Thread(this);

    String playerData;
    int playerCount = 0;
    int gameStage = WAITING_PLAYER;
    int numPlayers;

    ArrayList<Point> field = new ArrayList<Point>();
    ArrayList<Point> bricks = new ArrayList<Point>();

    // scans for the total number of players needed - initializes the game server
    public static void main(String args[]) {
        System.out.print("Enter number of players needed: ");

        Scanner scanner = new Scanner(System.in);
        int totalPlayers = scanner.nextInt();

        new GameServer(totalPlayers);
    }

    // initializes the state, creates a datagram socket, and starts the thread
    public GameServer(int numPlayers) {
        this.numPlayers = numPlayers;

        try {
            serverSocket = new DatagramSocket(portNumber);
        } catch (SocketException e) {
            System.out.println("Could not listen on port: " + portNumber);
            System.exit(0);
        }

        game = new ServerState();
        System.out.println("Game created...");
        thread.start();
    }

    // the thread that has all the functions of the game server
    public void run() {
        while(true) {

            //fetches the message sent by a client
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                serverSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            playerData = new String(buf);
            playerData.trim();

            if (gameStage == WAITING_PLAYER) { // if the game is still waiting for players
                if (playerData.startsWith("CONNECT")) {
                    // gets the data of the player and puts it in the hashmap containing all the players
                    String tokens[] = playerData.split(" ");
                    PlayerDetail player = new PlayerDetail(tokens[1], packet.getAddress(), packet.getPort());
                    System.out.println("Player " + tokens[1] + " is now connected!");
                    game.updatePlayers(tokens[1].trim(), player);

                    // broadcasts to the players that a new player has been connected
                    broadcast("CONNECTED " + tokens[1]);

                    // gets the coordinates of the mines set by a player and adds it to an arraylist
                    String[] coordinates = tokens[2].split(";");
                    for(int i=0; i < coordinates.length - 1; i++) {
                        String[] xy = coordinates[i].split(",");
                        Point tempPoint = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
                        field.add(tempPoint);
                    }

                    // increase the player count and check if the game is about the start
                    playerCount = playerCount + 1;
                    if (playerCount == numPlayers) {
                        gameStage = GAME_START;
                    }
                }
            } else if (gameStage == GAME_START) { //if the actual game is about to start
                // randomizes a coordinate for a brick that will not collide with an existing brick and/or mine
                Random rand = new Random();
                int total = numPlayers * 4;
                String bricksLocation = "";

                while (bricks.size() != total) {
                    boolean contains = false;
                    int tempX = rand.nextInt(1220);
                    int tempY = rand.nextInt(645);

                    for(int i=0; i < field.size(); i++) {
                        if (tempX <= field.get(i).x + 60 && tempX >= field.get(i).x - 60 && tempY <= field.get(i).y + 75 && tempY >= field.get(i).y - 75) {
                            contains = true;
                        }
                    }

                    for(int i=0; i < bricks.size(); i++) {
                        if (tempX <= bricks.get(i).x + 60 && tempX >= bricks.get(i).x - 60 && tempY <= bricks.get(i).y + 75 && tempY >= bricks.get(i).y - 75) {
                            contains = true;
                        }
                    }

                    if (contains != true) {
                        Point tempP = new Point(tempX, tempY);
                        bricks.add(tempP);
                        bricksLocation = bricksLocation + tempX + "," + tempY + ";";
                    }
                }

                // randomizes among the available bricks to get the location of the flag
                int flag = rand.nextInt(bricks.size());
                int flagX = bricks.get(flag).x;
                int flagY = bricks.get(flag).y;

                // broadcasts to the player the location of the bricks, flag, and the mines
                broadcast("START " + bricksLocation + " " + flagX + "," + flagY + " " + getMine());

                // change the stage to start the game
                gameStage = IN_PROGRESS;
            } else if (gameStage == IN_PROGRESS) { // if the game is currently in progress
                if (playerData.startsWith("PLAYER")) { // if the players are sending details
                    // get the details of game of a player and updates the currently existing values
                    String[] playerInfo = playerData.split(" ");
                    String playerName = playerInfo[1];
                    int x = Integer.parseInt(playerInfo[2].trim());
                    int y = Integer.parseInt(playerInfo[3].trim());
                    int face = Integer.parseInt(playerInfo[4].trim());
                    int life = Integer.parseInt(playerInfo[5].trim());
                    int openX = Integer.parseInt(playerInfo[6].trim());
                    int openY = Integer.parseInt(playerInfo[7].trim());
                    int type = Integer.parseInt(playerInfo[8].trim());

                    if (type == 1 || type == 0) {
                        System.out.println(playerData);
                    }

                    PlayerDetail player = (PlayerDetail) game.getPlayers().get(playerName);
                    player.setX(x);
                    player.setY(y);
                    player.setFace(face);
                    player.setLife(life);
                    player.setOpenX(openX);
                    player.setOpenY(openY);
                    player.setOpenType(type);

                    game.updatePlayers(playerName, player);
                    broadcast(game.getDetails()); // sends the updated data to the different players
                } else if (playerData.startsWith("WINNER")) { // if a player has won the game
                    broadcast(playerData); // broadcast to the others the name of the winner
                } else if (playerData.startsWith("LOSE")) { // if a player lost all of his/her lives
                    // set the life of that player to 0
                    String[] temp = playerData.split(" ");
                    PlayerDetail p1 = (PlayerDetail) game.getPlayers().get(temp[1].trim());
                    p1.setLife(0);
                    game.updatePlayers(temp[1], p1);

                    // checks to see if all of the players except one is already dead
                    int validPlayers = 0;
                    String winner = "";
                    for(Iterator ite = game.getPlayers().keySet().iterator(); ite.hasNext();) {
                        String name = (String) ite.next();
                        PlayerDetail player = (PlayerDetail) game.getPlayers().get(name);
                        if (player.getLife() == 0) {
                            validPlayers = validPlayers + 1;
                        } else {
                            winner = name;
                        }
                    }

                    if (game.getPlayers().size() - validPlayers == 1) {
                        broadcast("WINNER " + winner); // broadcast the remaining one as the winner
                    }
                }
            }
        }
    }

    // function for getting all the available coordinates for the mines
    public String getMine() {
        String mineCoordinates = "";
        for(int i=0; i < field.size(); i++) {
            mineCoordinates = mineCoordinates + field.get(i).x + "," + field.get(i).y + ";";
        }
        return mineCoordinates;
    }

    // sends a message to all the clients
    public void broadcast(String message) {
        // for all available player, send the necessary data
        for(Iterator ite = game.getPlayers().keySet().iterator(); ite.hasNext();) {
            String name = (String) ite.next();
            PlayerDetail player = (PlayerDetail) game.getPlayers().get(name);
            send(player, message);
        }
    }

    // sends a message to a client
    public void send(PlayerDetail player, String message) {
        DatagramPacket packet;
        byte buf[] = message.getBytes();
        packet = new DatagramPacket(buf, buf.length, player.getAddress(), player.getPort());

        try {
            serverSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
