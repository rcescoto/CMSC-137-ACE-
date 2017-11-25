package javagame;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Play extends BasicGameState implements Runnable {
    Animation charac, mUp, mDown, mLeft, mRight, sample;
    int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    int face = 1;

    Image background;

    boolean quit = false;
    int[] duration = {200, 200};

    private int first = 1;

    public String taunt = "Move!";
    public String charName = "";

    int playerX = 0;
    int playerY = 0;

    String server = "localhost";
    boolean connected = false;
    DatagramSocket socket = new DatagramSocket();
    String serverData = "";
    Thread t = new Thread(this);

    ArrayList<String> players = new ArrayList<String>();

    public Play(int state, String name) throws SocketException {
        this.charName = name;
        t.start();
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (connected != true && serverData.startsWith("CONNECTED")) {
                connected = true;
            } else if (connected != true) {
                send("CONNECT " + charName);
            } else if (connected == true) {
                if(serverData.startsWith("PLAYER")) {
                    players.clear();
                    String[] playersInfo = serverData.split(" : ");
                    for(int i=0; i < playersInfo.length; i++) {
                        players.add(playersInfo[i]);
                    }
                }
            }

            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            serverData = new String(buf);
            serverData = serverData.trim();
        }
    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{background = new Image("grassbg.jpg");
        Image[] moveUp = {new Image("isaacsBack.png"), new Image("isaacsBack.png")};
        Image[] moveDown = {new Image("isaacsFront.png"), new Image("isaacsFront.png")};
        Image[] moveLeft = {new Image("isaacsLeft.png"), new Image("isaacsLeft.png")};
        Image[] moveRight = {new Image("isaacsRight.png"), new Image("isaacsRight.png")};

        mUp = new Animation(moveUp, duration, false);
        mDown = new Animation(moveDown, duration, false);
        mLeft = new Animation(moveLeft, duration, false);
        mRight = new Animation(moveRight, duration, false);

        charac = mDown;
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
       background.draw(0,0);

       charac.draw(playerX,playerY);
       g.drawString(charName, playerX-50, playerY+30);

       for(int i=0; i < players.size(); i++) {
           String info[] = (players.get(i)).split(" ");
           String playerName = info[1];
           int x = Integer.parseInt(info[2]);
           int y = Integer.parseInt(info[3]);
           int tempFace = Integer.parseInt(info[4]);

           Animation temp = mDown;
           if (tempFace == UP){
               temp = mUp;
           } else if (tempFace == DOWN) {
               temp = mDown;
           } else if (tempFace == LEFT) {
               temp = mLeft;
           } else if (tempFace == RIGHT) {
               temp = mRight;
           }

           temp.draw(x, y);
           g.drawString(playerName, x-50, y+30);
       }

        if(quit == true){
           g.drawString("Continue (Enter)", 600, 250);
           g.drawString("Go to Menu (M)", 600, 350);
           g.drawString("Exit Game (E)", 600, 450);
           if(quit == false){
               g.clear();
           }
       }

       g.drawString(taunt, 640, 0);
    }

    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{
        int prevX = playerX;
        int prevY = playerY;

        Input move = gc.getInput();
        if(move.isKeyDown(Input.KEY_DOWN)&& quit == false && playerY < 649){
            charac = mDown;
            playerY += 1;
            taunt = "Pointing at x: " + playerX + " y " + playerY;
            face = DOWN;
        }
        else if(move.isKeyDown(Input.KEY_UP)&& quit == false && playerY >0){
            charac = mUp;
            playerY -= 1;
            taunt = "Pointing at x: " + playerX + " y " + playerY;
            face = UP;
        }
        else if(move.isKeyDown(Input.KEY_LEFT)&& quit == false&& playerX  > 0){
            charac = mLeft;
            playerX -= 1;
            taunt = "Pointing at x: " + playerX + " y " + playerY;
            face = LEFT;
        }
        else if(move.isKeyDown(Input.KEY_RIGHT)&& quit == false&& playerX< 1225){
            charac = mRight;
            playerX += 1;
            taunt = "Pointing at x: " + playerX + " y " + playerY;
            face = RIGHT;
        }
        else if(move.isKeyDown(Input.KEY_ESCAPE)){
            quit = true;
        }

        if (prevX != playerX || prevY != playerY) {
            send("PLAYER " + charName + " " + playerX + " " + playerY + " " + face);
        }

        if(quit == true){
            if(move.isKeyDown(Input.KEY_ENTER)){
                quit = false;
            }
            else if(move.isKeyDown(Input.KEY_M)){
                quit = false;
                charac = mDown;
                playerX = 0;
                playerY = 0;
                sbg.enterState(0);
            }
            else if(move.isKeyDown(Input.KEY_E)){
                System.exit(0);
            }
        }
    }

    public int getID(){
        return 1;
    }

    public void send(String message) {
        try {
            byte[] buf = message.getBytes();
            InetAddress address = InetAddress.getByName(server);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4444);
            socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("HELLO IO SA SEND");
            e.printStackTrace();
        }

    }
}
