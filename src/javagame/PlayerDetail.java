package javagame;

import java.net.InetAddress;

public class PlayerDetail {
    InetAddress address;
    int port;
    String charName;
    int x = 0, y = 0, face = 0;

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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public String getDetails() {
        String value = "";
        value = value + "PLAYER " + charName + " " + x + " " + y + " " + face;
        return value;
    }
}