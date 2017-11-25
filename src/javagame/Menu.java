package javagame;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Menu extends BasicGameState{
    //constructor
    Image start;
    Image exit;
    public String title = "It's Mine";

    public Menu(int state){
    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{
        start = new Image("playNow.png");
        exit = new Image("exitGame.png");
    }
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
    //draw here for components inside the game
        g.drawString(title, 600, 100);
        start.draw(555, 150);//200X30
        exit.draw(555, 200);
    }
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{
        //Input click = gc.getInput();
        int xpos = Mouse.getX();
        int ypos = Mouse.getY();
        if((xpos>555 && xpos<755)&&(ypos>540 && ypos<570)){
            if(Mouse.isButtonDown(0)){
                sbg.enterState(1);
            }
        }
        else if((xpos>555 && xpos<755)&&(ypos>490 && ypos<520)){
            if(Mouse.isButtonDown(0)){
               System.exit(0);
            }
        }
    }
    public int getID(){
        return 0;
    }

}
