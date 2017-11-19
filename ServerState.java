package javagame;

import java.util.HashMap;
import java.util.Iterator;

public class ServerState {
    private HashMap<String, PlayerDetail> players = new HashMap<String, PlayerDetail>();
    public ServerState(){}

    public void updatePlayers(String name, PlayerDetail player) {
        players.put(name, player);
    }

    public HashMap getPlayers() {
        return players;
    }

    public String getDetails() {
        String value = "";
        for(Iterator ite = players.keySet().iterator(); ite.hasNext();) {
            String name = (String) ite.next();
            PlayerDetail player = (PlayerDetail) players.get(name);
            value = value + player.getDetails() + " : ";
        }
        return value;
    }
}
