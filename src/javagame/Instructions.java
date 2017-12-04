package javagame;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

// INSTRUCTIONS STATE - tells how to play the game
public class Instructions extends BasicGameState {
    Image background;
    Image bricks, characters, digs, flag, mines;

    public Instructions(int state){
    }

    // initializes the components that will be used for the instruction state
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        background = new Image("map.png");
        bricks = new Image("bricks.png");
        characters = new Image("characters.png");
        digs = new Image("digs.png");
        flag = new Image("flag.png");
        mines = new Image("mines.png");
    }

    // renders/draws the components used in the instruction state
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException{
        g.drawString("IT'S MINE!", 600, 100);
        background.draw(0,0);
        g.drawString("INSTRUCTIONS", 580, 80);
        g.drawString("SETUP MINES: ", 80, 110);
        g.drawString("Use the arrow keys (up, down, left, right) to move the mine.", 100, 130);
        g.drawString("Use space bar to put the mine in the chosen position. You need to choose locations for 3 mines.", 100, 150);

        mines.draw(1000, 100);
        bricks.draw(830, 200);
        flag.draw(850, 330);
        characters.draw(200, 430);
        digs.draw(900, 430);

        g.drawString("MAIN GAME: ", 80, 200);
        g.drawString("Use the arrow keys (up, down, left, right) to move the character.", 100, 220);
        g.drawString("Use the space bar to break the brick and check to see if it has the flag.", 100, 240);

        g.drawString("TO WIN: Be the last one standing or find the flag!", 80, 290);

        g.drawString("WARNING: Watch where you're going! Remember where you put your mines.", 80, 340);
        g.drawString("Each time you step on a mine, you lose a life and you only have 2 lives.", 150, 360 );
        g.drawString("Also, some of the bricks has traps. Goodluck!", 150, 380);
        g.drawString("Press Enter to proceed to the game...", 500, 480);
    }

    // checks whether the button 'Enter' has been pressed to proceed to the actual game
    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException{
        Input enter = gc.getInput();
        if(enter.isKeyDown(Input.KEY_ENTER)){
            sbg.enterState(1);
        }
    }

    // returns the state number for the instruction state
    public int getID(){
        return 2;
    }
}
