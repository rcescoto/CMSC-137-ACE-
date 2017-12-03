package javagame;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

import javax.swing.*;
import java.net.SocketException;

public class GameJava extends StateBasedGame{
    public static final String title = "IT'S MINE!";
    public static final int menu =0;
    public static final int play =1;
    public static final int instructions = 2;

    public String charName = "";
    public String localhost = "";

    public GameJava(String title){//constructor
        super(title);
        this.addState(new Menu(menu));

        localhost = JOptionPane.showInputDialog("Enter host: ");
        charName = JOptionPane.showInputDialog("Enter player name: ");
        try {
            this.addState(new Play(play, charName, localhost));
        } catch (SocketException e) {
            e.printStackTrace();
        }

        this.addState(new Instructions(instructions));
    }

    public void initStatesList(GameContainer gc)throws SlickException{
        this.getState(menu).init(gc, this);
        this.getState(play).init(gc, this);
        this.getState(instructions).init(gc, this);
        this.enterState(menu);
    }


    public static void main(String[] args){
        AppGameContainer appgc;

        try{
            appgc = new AppGameContainer(new GameJava(title));
            appgc.setDisplayMode(1280, 720, false);
            appgc.start();
        }
        catch(SlickException e){
            e.printStackTrace();
        }
    }
}
