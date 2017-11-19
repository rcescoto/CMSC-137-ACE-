package javagame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Scanner;

public class GameServer implements Runnable {
    public final int WAITING_PLAYER = 0;
    public final int GAME_START = 1;
    public final int IN_PROGRESS = 2;
    public final int GAME_END = 3;
    public int portNumber = 4444;

    String playerData;
    int playerCount = 0;
    DatagramSocket serverSocket = null;
    ServerState game;
    int gameStage = WAITING_PLAYER;
    int numPlayers;
    Thread thread = new Thread(this);

    public static void main(String args[]) {
        System.out.print("Enter number of players needed: ");

        Scanner scanner = new Scanner(System.in);
        int totalPlayers = scanner.nextInt();

        new GameServer(totalPlayers);
    }

    public GameServer(int numPlayers) {
        this.numPlayers = numPlayers;

        try {
            serverSocket = new DatagramSocket(portNumber);
        } catch (SocketException e) {
            System.out.println("Could not listen on port" + portNumber);
            System.exit(0);
        }

        game = new ServerState();
        System.out.println("Game created...");
        thread.start();
    }

    public void run() {
        while(true) {
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                serverSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            playerData = new String(buf);
            playerData.trim();

            if (gameStage == WAITING_PLAYER) {
                if (playerData.startsWith("CONNECT")) {
                    String tokens[] = playerData.split(" ");
                    PlayerDetail player = new PlayerDetail(tokens[1], packet.getAddress(), packet.getPort());
                    System.out.println("Player " + tokens[1] + "is now connected!");
                    game.updatePlayers(tokens[1].trim(), player);
                    broadcast("CONNECTED " + tokens[1]);
                    playerCount = playerCount + 1;
                    if (playerCount == numPlayers) {
                        gameStage = GAME_START;
                    }
                }
            } else if (gameStage == GAME_START) {
                System.out.println("Game state: START");
                broadcast("START");
                gameStage = IN_PROGRESS;
                break;
            } else if (gameStage == IN_PROGRESS) {
                if (playerData.startsWith("PLAYER")) {
                    String[] playerInfo = playerData.split(" ");
                    String playerName = playerInfo[1];
                    int x = Integer.parseInt(playerInfo[2].trim());
                    int y = Integer.parseInt(playerInfo[3].trim());

                    PlayerDetail player = (PlayerDetail) game.getPlayers().get(playerName);
                    player.setX(x);
                    player.setY(y);
                    game.updatePlayers(playerName, player);
                    broadcast(game.getDetails());
                }
            }
        }
    }

    public void broadcast(String message) {
        for(Iterator ite = game.getPlayers().keySet().iterator(); ite.hasNext();) {
            String name = (String) ite.next();
            PlayerDetail player = (PlayerDetail) game.getPlayers().get(name);
            send(player, message);
        }
    }

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
