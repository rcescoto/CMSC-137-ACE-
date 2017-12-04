package javagame;

import java.net.InetAddress;

// CLASS FOR THE DETAILS OF A PLAYER: setters and getters for the different attributes of the player
public class PlayerDetail {
    InetAddress address;
    int port;
    String charName;
    int x = 0, y = 0, face = 0, life = 0, openX = 0, openY = 0, openType = 2;

    public PlayerDetail(String name, InetAddress address, int port) {
        this.charName = name;
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return charName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getFace() {
        return face;
    }

    public int getLife() { return life; }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public void setLife(int life) { this.life = life; }

    public void setOpenX(int x) { this.openX = x; }

    public void setOpenY(int y) { this.openY = y; }

    public void setOpenType(int type) { this.openType = type; }

    public String getDetails() {
        String value = "";
        value = value + "PLAYER " + charName + " " + x + " " + y + " " + face + " " + life + " " + openX + " " + openY + " " + openType;
        return value;
    }
}
