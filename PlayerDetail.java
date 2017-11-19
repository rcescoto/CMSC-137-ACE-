package javagame;

import java.net.InetAddress;

public class PlayerDetail {
    InetAddress address;
    int port;
    String charName;
    int x, y;

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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getDetails() {
        String value = "";
        value = value + "PLAYER " + charName + " " + x + " " + y + " ";
        return value;
    }
}
