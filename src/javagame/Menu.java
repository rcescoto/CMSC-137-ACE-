package javagame;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

// MENU STATE - the starting page of the game
public class Menu extends BasicGameState{
    //constructor
    Image start;
    Image exit;
    public String title = "It's Mine";

    public Menu(int state){
    }

    // initializes the options found in the menu
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException{
        start = new Image("playNow.png");
        exit = new Image("exitGame.png");
    }

    //draws the components for the menu
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
        g.drawString(title, 600, 100);
        start.draw(555, 150);
        exit.draw(555, 200);
    }

    // checks whether an option has been clicked and if so, does the appropriate action
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{
        int xpos = Mouse.getX();
        int ypos = Mouse.getY();
        if((xpos>555 && xpos<755)&&(ypos>540 && ypos<570)){
            if(Mouse.isButtonDown(0)){
                sbg.enterState(2);
            }
        }
        else if((xpos>555 && xpos<755)&&(ypos>490 && ypos<520)){
            if(Mouse.isButtonDown(0)){
               System.exit(0);
            }
        }
    }

    // returns the state number of the menu
    public int getID(){
        return 0;
    }
}
