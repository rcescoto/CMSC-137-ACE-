import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//State to nung mga players - para makeep track mo kung sino na ang alin plus ung data nila
public class GameState{
	// player name,NetPlayer


	//dedeclare ka ng players na hashmap
	private Map players=new HashMap();
	public GameState(){}
	
	//uupdate mo ung hashmap, either maglagay ka ng bago or uupdate mo ung luma
	public void update(String name, NetPlayer player){
		players.put(name,player);
	}
	
	//lahat ng data ng players kunin at ilagay sa string
	public String toString(){
		String retval="";
		for(Iterator ite=players.keySet().iterator();ite.hasNext();){
			String name=(String)ite.next();
			NetPlayer player=(NetPlayer)players.get(name);
			retval+=player.toString()+":";
		}
		return retval;
	}

	//kukunin mo ung hashmap with all its data
	public Map getPlayers(){
		return players;
	}
}
