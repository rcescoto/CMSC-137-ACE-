package javagame;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Play extends BasicGameState{

    Animation charac, mUp, mDown, mLeft, mRight;

    Image background;

    boolean quit = false;
    int[] duration = {200, 200};

    private int first = 1;

    public String taunt = "Move!";

    float playerX = 0;
    float playerY = 0;

    public Play(int state){
    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{
        background = new Image("grassbg.jpg");
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

        Input move = gc.getInput();
        if(move.isKeyDown(Input.KEY_DOWN)&& quit == false && playerY < 649){
            charac = mDown;
            playerY += 1;
            taunt = "Pointing at x: " + playerX + " y " + playerY;
        }
        else if(move.isKeyDown(Input.KEY_UP)&& quit == false && playerY >0){
            charac = mUp;
            playerY -= 1;
            taunt = "Pointing at x: " + playerX + " y " + playerY;

        }
        else if(move.isKeyDown(Input.KEY_LEFT)&& quit == false&& playerX  > 0){
            charac = mLeft;
            playerX -= 1;
            taunt = "Pointing at x: " + playerX + " y " + playerY;

        }
        else if(move.isKeyDown(Input.KEY_RIGHT)&& quit == false&& playerX< 1225){
            charac = mRight;
            playerX += 1;
            taunt = "Pointing at x: " + playerX + " y " + playerY;

        }
        else if(move.isKeyDown(Input.KEY_ESCAPE)){
            quit = true;
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

}
