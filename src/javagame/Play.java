package javagame;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

// PLAY STATE - the state containing the actual game play of the game
public class Play extends BasicGameState implements Runnable {
    Animation charac, mUp, mDown, mLeft, mRight, dLeft, dRight, dUp;
    Animation explosionSetup, explosion, brokenSetup, broken, mine, mineSetup, brickSetup, brick, flag, flagSetup;
    Image background;

    int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    int BRICK = 0, MINE = 1;
    int SETUP_MINES = 0, START_GAME = 1, GAME_END = 2;

    boolean quit = false;
    int gameState = SETUP_MINES;
    int[] duration = {200, 200};

    public String charName = "";
    String server = "localhost";

    int playerX, playerY = 0;
    int mineX, mineY = 0;

    boolean connected = false;

    DatagramSocket socket = new DatagramSocket();
    String serverData = "";
    Thread t = new Thread(this);

    ArrayList<String> players = new ArrayList<String>();
    ArrayList<Point> minefield = new ArrayList<Point>();
    ArrayList<Point> bricks = new ArrayList<Point>();
    Point flagCoordinate;
    Point opened = null;
    String winner = "null";
    String lose = "null";
    int life = 2;
    int openType = 2;
    int face = 1;

    public Play(int state, String name, String localhost) throws SocketException {
        this.charName = name;
        this.server = localhost;
        t.start();
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (gameState == START_GAME) {
                if (connected != true && serverData.startsWith("CONNECTED")) {
                    connected = true;
                } else if (connected != true) {
                    String points = "";
                    for(int i=0; i < minefield.size(); i++) {
                        points = points + minefield.get(i).x + "," + minefield.get(i).y + ";";
                    }
                    send("CONNECT " + charName + " " + points);
                } else if (connected == true) {
                    if(serverData.startsWith("PLAYER")) {
                        players.clear();
                        String[] playersInfo = serverData.split(" : ");
                        for(int i=0; i < playersInfo.length; i++) {
                            players.add(playersInfo[i]);
                        }
                    } else if (serverData.startsWith("START")) {
                        String[] temp = serverData.split(" ");

                        String[] tempBricks = temp[1].split(";");
                        for(int i=0; i < tempBricks.length  ; i++) {
                            String[] points = tempBricks[i].split(",");
                            Point coordinate = new Point (Integer.parseInt(points[0]), Integer.parseInt(points[1]));
                            bricks.add(coordinate);
                        }

                        String[] tempFlag = temp[2].split(",");
                        flagCoordinate = new Point(Integer.parseInt(tempFlag[0]), Integer.parseInt(tempFlag[1]));

                        minefield.clear();
                        String[] tempMine = temp[3].split(";");
                        for(int i=0; i < tempMine.length; i++) {
                            String[] points = tempMine[i].split(",");
                            Point coordinate = new Point(Integer.parseInt(points[0]), Integer.parseInt(points[1]));
                            minefield.add(coordinate);
                        }
                    } else if (serverData.startsWith("WINNER")) {
                        String[] win = serverData.split(" ");
                        winner = win[1];
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
    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{
        background = new Image("map.png");
        Image[] moveUp = {new Image("isaacsBack.png"), new Image("isaacsBack.png")};
        Image[] moveDown = {new Image("isaacsFront.png"), new Image("isaacsFront.png")};
        Image[] moveLeft = {new Image("isaacsLeft.png"), new Image("isaacsLeft.png")};
        Image[] moveRight = {new Image("isaacsRight.png"), new Image("isaacsRight.png")};

        mUp = new Animation(moveUp, duration, false);
        mDown = new Animation(moveDown, duration, false);
        mLeft = new Animation(moveLeft, duration, false);
        mRight = new Animation(moveRight, duration, false);

        charac = mDown;

        Image[] digUp = {new Image("digFront.png"), new Image("digFront.png")};
        Image[] digLeft = {new Image("digLeft.png"), new Image("digLeft.png")};
        Image[] digRight = {new Image("digRight.png"), new Image("digRight.png")};

        dUp = new Animation(digUp, duration, false);
        dLeft = new Animation(digLeft, duration, false);
        dRight = new Animation(digRight, duration, false);

        Image[] mineImg = {new Image("mine.png"), new Image("mine.png")};
        mineSetup = new Animation(mineImg, duration, false);
        mine = mineSetup;

        Image[] brickImg = {new Image("brick.png"), new Image("brick.png")};
        brickSetup = new Animation(brickImg, duration, false);
        brick = brickSetup;

        Image[] flagImg = {new Image("flag.png"), new Image("flag.png") };
        flagSetup = new Animation(flagImg, duration, false);
        flag = flagSetup;

        Image[] explosionImg = {new Image("explosion.png"), new Image("explosion.png")};
        explosionSetup = new Animation(explosionImg, duration, false);
        explosion = explosionSetup;

        Image[] brokenImg = {new Image("break.png"), new Image("break.png")};
        brokenSetup = new Animation(brokenImg, duration, false);
        broken = brokenSetup;
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
       background.draw(0,0);

       if (gameState == START_GAME) {
           g.drawString("Use arrow keys to move and space bar to break the bricks.", 150, 10);
           charac.draw(playerX,playerY);
           g.drawString(charName, playerX, playerY-20);

           int row = 10;
           for(int i=0; i < players.size(); i++) {
               String info[] = (players.get(i)).split(" ");
               String playerName = info[1];
               int x = Integer.parseInt(info[2]);
               int y = Integer.parseInt(info[3]);
               int tempFace = Integer.parseInt(info[4]);
               int tempLife = Integer.parseInt(info[5]);
               int tempOpenX = Integer.parseInt(info[6]);
               int tempOpenY = Integer.parseInt(info[7]);
               int tempType = Integer.parseInt(info[8]);

               g.drawString(tempLife + " - " + playerName, 1180, row);
               row = row + 20;

               if (tempType == 0) {
                   System.out.println("BOI BRICKS");
                   for (int j = 0; j < bricks.size(); j++) {
                       if (tempOpenX == bricks.get(j).x && tempOpenY == bricks.get(j).y) {
                           broken.draw(tempOpenX, tempOpenY);
                           bricks.remove(j);
                           System.out.println("YAS BRICKS");
                           break;
                       }
                   }
               } else if (tempType == 1) {
                   System.out.println("BOI MINE");
                   for(int j=0; j < minefield.size(); j++) {
                       System.out.println(playerName + " " + tempOpenX + " vs " + minefield.get(j).x + " " + tempOpenY + " vs " + minefield.get(j).y);
                       if (tempOpenX == minefield.get(j).x && tempOpenY == minefield.get(j).y) {
                           System.out.println("YAS MINE");
                           minefield.remove(j);
                           explosion.draw(tempOpenX, tempOpenY);
                           break;
                       }
                   }
               }

               if (tempLife != 0) {
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
                   g.drawString(playerName, x, y-20);
               }
           }

           if (opened != null && flagCoordinate != null) {
               if (opened.x == flagCoordinate.x && opened.y == flagCoordinate.y) {
                   flag.draw(flagCoordinate.x, flagCoordinate.y);
                   winner = charName;
               } else {
                   if (openType == MINE) {
                       explosion.draw(opened.x, opened.y);
                   } else if (openType == BRICK) {
                       broken.draw(opened.x, opened.y);
                   }
               }
           }

           for(int i=0; i < bricks.size(); i++) {
               if (bricks.get(i).x == flagCoordinate.x && bricks.get(i).y == flagCoordinate.y) {
                   flag.draw(bricks.get(i).x, bricks.get(i).y);
               } else {
                   brick.draw(bricks.get(i).x, bricks.get(i).y);
               }
           }

           for(int i=0; i < minefield.size(); i++) {
               mine.draw(minefield.get(i).x, minefield.get(i).y);
           }
       } else if (gameState == SETUP_MINES) {
            g.drawString("Set up 3 mines around the field: Use the arrow keys for controls and use space bar to put mine in a location.", 150, 10);
            mine.draw(mineX, mineY);

            for(int i=0; i < minefield.size(); i++) {
                mine.draw(minefield.get(i).x, minefield.get(i).y);
            }

            if (minefield.size() == 3) {
                gameState = START_GAME;
            }
       }

       if (winner != "null") {
           g.clear();
           if (winner.equals(charName)) {
               g.drawString("Congratulations " + winner + " you've won the game!", 450, 360);
           } else {
               g.drawString("You lose. " + winner + " has won the game.", 500, 360);
           }
       }

       if (lose != "null") {
           g.clear();
           g.drawString("You've lost all your lives. You lose.", 500, 360);
       }

        if(quit == true){
           g.drawString("Continue (Enter)", 600, 150);
           g.drawString("Instructions (I)", 600, 250);
           g.drawString("Go to Menu (M)", 600, 350);
           g.drawString("Exit Game (E)", 600, 450);
           if(quit == false){
               g.clear();
           }
       }
    }

    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{
        int prevX = playerX;
        int prevY = playerY;

        Input move = gc.getInput();

        if (gameState == START_GAME) {
            int bounds = checkBounds(playerX, playerY);
            if(move.isKeyDown(Input.KEY_DOWN)&& quit == false && playerY < 649 && bounds != UP){
                charac = mDown;
                playerY += 1;
                face = DOWN;
            }
            else if(move.isKeyDown(Input.KEY_UP)&& quit == false && playerY > 0 && bounds != DOWN){
                charac = mUp;
                playerY -= 1;
                face = UP;
            }
            else if(move.isKeyDown(Input.KEY_LEFT)&& quit == false&& playerX  > 0 && bounds != RIGHT){
                charac = mLeft;
                playerX -= 1;
                face = LEFT;
            }
            else if(move.isKeyDown(Input.KEY_RIGHT)&& quit == false&& playerX < 1225 && bounds != LEFT){
                charac = mRight;
                playerX += 1;
                face = RIGHT;
            }
            else if (move.isKeyDown(Input.KEY_SPACE) && quit == false) {
                if (bounds == UP && face == DOWN) {
                    charac = dUp;
                    removeBrick(playerX, playerY);
                } else if (bounds == DOWN && face == UP) {
                    removeBrick(playerX, playerY);
                } else if (bounds == LEFT && face == RIGHT) {
                    charac = dRight;
                    removeBrick(playerX, playerY);
                } else if (bounds == RIGHT && face == LEFT) {
                    charac = dLeft;
                    removeBrick(playerX, playerY);
                }

                if (opened != null) {
                    if (opened.x != flagCoordinate.x && opened.y != flagCoordinate.y) {
                        send("PLAYER " + charName + " " + playerX + " " + playerY + " " + face + " " + life + " " + opened.x + " " + opened.y + " " + openType);
                    } else {
                        send("WINNER " + charName);
                    }
                }
            }
            else if(move.isKeyDown(Input.KEY_ESCAPE)){
                quit = true;
            }

            if ((prevX != playerX || prevY != playerY) && winner == "null") {
                int prevLife = life;
                for(int i=0; i < minefield.size(); i++) {
                    if (playerX <= minefield.get(i).x + 10 && playerX >= minefield.get(i).x - 10 && playerY <= minefield.get(i).y + 10 && playerY >= minefield.get(i).y - 10) {
                        opened = null;
                        openType = MINE;
                        opened = new Point(minefield.get(i).x, minefield.get(i).y);
                        minefield.remove(i);
                        life = life - 1;
                    }
                }

                if (life == 0) {
                    send("LOSE " + charName);
                    lose = charName;
                } else if (life < prevLife){
                    send("PLAYER " + charName + " " + playerX + " " + playerY + " " + face + " " + life + " " + opened.x + " " + opened.y + " " + openType);
                } else {
                    send("PLAYER " + charName + " " + playerX + " " + playerY + " " + face + " " + life + " " + -1 + " " + -1 + " " + 2);
                }
            }
        } else if (gameState == SETUP_MINES) {
            if(move.isKeyDown(Input.KEY_DOWN)&& quit == false && mineY < 649){
                mineY += 1;
            } else if(move.isKeyDown(Input.KEY_UP)&& quit == false && mineY >0){
                mineY -= 1;
            } else if(move.isKeyDown(Input.KEY_LEFT)&& quit == false&& mineX  > 0){
                mineX -= 1;
            } else if(move.isKeyDown(Input.KEY_RIGHT)&& quit == false&& mineX < 1225){
                mineX += 1;
            } else if (move.isKeyDown(Input.KEY_SPACE) && quit == false) {
                Point tempPoint = new Point(mineX, mineY);
                boolean contains = false;
                for(int i=0; i < minefield.size(); i++) {
                    if (minefield.get(i).x == mineX && minefield.get(i).y == mineY) {
                        contains = true;
                    }
                }

                if (contains == false) {
                    minefield.add(tempPoint);
                }
            } else if(move.isKeyDown(Input.KEY_ESCAPE)){
                quit = true;
            }
        }

        if(quit == true){
            if(move.isKeyDown(Input.KEY_ENTER)){
                quit = false;
            }
            else if(move.isKeyDown(Input.KEY_I)) {
                quit = false;
                sbg.enterState(2);
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

    public void send(String message) {
        try {
            byte[] buf = message.getBytes();
            InetAddress address = InetAddress.getByName(server);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 5559);
            socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public int checkBounds(int x, int y) {
        int bounds = 4;
        for(int i=0; i < bricks.size(); i++) {
            if (x == bricks.get(i).x + 55 && y <= bricks.get(i).y+70 && y >= bricks.get(i).y-70) {
                bounds = RIGHT;
            } else if (x == bricks.get(i).x - 55 && y <= bricks.get(i).y+70 && y >= bricks.get(i).y-70) {
                bounds = LEFT;
            } else if (y == bricks.get(i).y + 70 && x <= bricks.get(i).x+55 && x >= bricks.get(i).x-55) {
                bounds = DOWN;
            } else if (y == bricks.get(i).y - 70 && x <= bricks.get(i).x+55 && x >= bricks.get(i).x-55) {
                bounds = UP;
            }
        }
        return bounds;
    }

    public void removeBrick(int x, int y) {
        opened = null;
        for(int i=0; i < bricks.size(); i++) {
            if (x == bricks.get(i).x + 55 && y <= bricks.get(i).y+60 && y >= bricks.get(i).y-15) {
                openType = BRICK;
                opened = new Point(bricks.get(i).x, bricks.get(i).y);
                bricks.remove(i);
            } else if (x == bricks.get(i).x - 55 && y <= bricks.get(i).y+60 && y >= bricks.get(i).y-15) {
                openType = BRICK;
                opened = new Point(bricks.get(i).x, bricks.get(i).y);
                bricks.remove(i);
            } else if (y == bricks.get(i).y + 70 && x <= bricks.get(i).x+60 && x >= bricks.get(i).x-10) {
                openType = BRICK;
                opened = new Point(bricks.get(i).x, bricks.get(i).y);
                bricks.remove(i);
            } else if (y == bricks.get(i).y - 70 && x <= bricks.get(i).x+60 && x >= bricks.get(i).x-10) {
                openType = BRICK;
                opened = new Point(bricks.get(i).x, bricks.get(i).y);
                bricks.remove(i);
            }
        }
    }

    public int getID(){
        return 1;
    }
}
