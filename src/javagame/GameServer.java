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

public class GameServer implements Runnable {
    public final int WAITING_PLAYER = 0;
    public final int GAME_START = 1;
    public final int IN_PROGRESS = 2;
    public final int GAME_END = 3;
    public int portNumber = 5555;

    String playerData;
    int playerCount = 0;
    DatagramSocket serverSocket = null;
    ServerState game;
    int gameStage = WAITING_PLAYER;
    int numPlayers;
    Thread thread = new Thread(this);
    ArrayList<Point> field = new ArrayList<Point>();
    ArrayList<Point> bricks = new ArrayList<Point>();
    Point flag;

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
            System.out.println("Could not listen on port: " + portNumber);
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
                    System.out.println("Player " + tokens[1] + " is now connected!");
                    game.updatePlayers(tokens[1].trim(), player);
                    broadcast("CONNECTED " + tokens[1]);

                    String[] coordinates = tokens[2].split(";");
                    for(int i=0; i < coordinates.length - 1; i++) {
                        String[] xy = coordinates[i].split(",");
                        Point tempPoint = new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
                        field.add(tempPoint);
                    }

                    playerCount = playerCount + 1;
                    if (playerCount == numPlayers) {
                        gameStage = GAME_START;
                    }
                }
            } else if (gameStage == GAME_START) {
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

                int flag = rand.nextInt(bricks.size());
                int flagX = bricks.get(flag).x;
                int flagY = bricks.get(flag).y;

                broadcast("START " + bricksLocation + " " + flagX + "," + flagY + " " + getMine());
                gameStage = IN_PROGRESS;
            } else if (gameStage == IN_PROGRESS) {
                if (playerData.startsWith("PLAYER")) {
                    System.out.println(playerData);
                    String[] info = playerData.split("-");
                    String[] playerInfo = info[0].split(" ");
                    String playerName = playerInfo[1];
                    int x = Integer.parseInt(playerInfo[2].trim());
                    int y = Integer.parseInt(playerInfo[3].trim());
                    int face = Integer.parseInt(playerInfo[4].trim());
                    int life = Integer.parseInt(playerInfo[5].trim());

                    PlayerDetail player = (PlayerDetail) game.getPlayers().get(playerName);
                    player.setX(x);
                    player.setY(y);
                    player.setFace(face);
                    player.setLife(life);
                    player.setField(info[1]);

                    game.updatePlayers(playerName, player);
                    broadcast(game.getDetails());
                } else if (playerData.startsWith("WINNER")) {
                    broadcast(playerData);
                } else if (playerData.startsWith("LOSE")) {
                    String[] temp = playerData.split(" ");
                    PlayerDetail p1 = (PlayerDetail) game.getPlayers().get(temp[1].trim());
                    System.out.println(p1.toString());
                    p1.setX(-1);
                    p1.setY(-1);
                    p1.setFace(-1);
                    game.updatePlayers(temp[1], p1);

                    int validPlayers = 0;
                    String winner = "";
                    for(Iterator ite = game.getPlayers().keySet().iterator(); ite.hasNext();) {
                        String name = (String) ite.next();
                        PlayerDetail player = (PlayerDetail) game.getPlayers().get(name);
                        if (player.getX() == -1 || player.getY() == -1 || player.getFace() == -1) {
                            validPlayers = validPlayers + 1;
                        } else {
                            winner = name;
                        }
                    }

                    if (game.getPlayers().size() - validPlayers == 1) {
                        broadcast("WINNER " + winner);
                    }
                }
            }
        }
    }

    public String getMine() {
        String mineCoordinates = "";
        for(int i=0; i < field.size(); i++) {
            mineCoordinates = mineCoordinates + field.get(i).x + "," + field.get(i).y + ";";
        }
        return mineCoordinates;
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
