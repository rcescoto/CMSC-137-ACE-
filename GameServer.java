import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//eto yung mismong parang base nung game server - nagiimplement lang siya ng thread saka ung constants na java file
public class GameServer implements Runnable, Constants{

	//declarations ng ibang importanteng variables
	String playerData; //eto yung nakukuha niyang dala from clients
	int playerCount=0; //eto ung pangcheck kung enough na ba yung client
	DatagramSocket serverSocket = null; //yung initial na serversocket
    GameState game; //declaration ng mismong state nung game
	int gameStage=WAITING_FOR_PLAYERS; //eto yung state habang nag-aantay pa siya ng enough clients
	int numPlayers; //bilang ng players
	Thread t = new Thread(this); //create ng thread

	//eto ung main na part, ang kumukuha lang siya ng parameters para sa kung ilan ung number ng player
	public static void main(String args[]){
		if (args.length != 1){
			System.out.println("Usage: java -jar circlewars-server <number of players>");
			System.exit(1);
		}
		
		//maggagawa siya ng gameserver tapos ipapasa niya ung kung ilang players
		new GameServer(Integer.parseInt(args[0]));
	}
	
	public GameServer(int numPlayers){ 
		this.numPlayers = numPlayers; //base dun sa pinasa, eto yung bilang ng pwedeng players (well, required)
		try {
            serverSocket = new DatagramSocket(PORT); //gagawa siya ng socket based sa port na nakadeclare sa constants
			serverSocket.setSoTimeout(100); //may timeout
		} catch (IOException e) {
            System.err.println("Could not listen on port: "+PORT); //error code kapag di makaconnect dun sa port
            System.exit(-1);
		}catch(Exception e){}

		game = new GameState(); //gagawa siya ng game state
		
		System.out.println("Game created..."); //print mo sa terminal na G na ung gamestate
		
		t.start(); //start na nung thread wohooo!
	}
	
	public void run(){
		while(true){
			byte[] buf = new byte[256]; //kelangan byte yung pinapasa using datagram packet kaya may ganito

			//declare ka ng datagrampacket na nagrereceive
			//ang kelangan niya ay yung message saka yung length
			DatagramPacket packet = new DatagramPacket(buf, buf.length); 
			
			//magrereceive ka using datagramsocket tas lalagay mo sa packets
			try{
     			serverSocket.receive(packet);
			}catch(Exception ioe){}
			

			//gagawin mong string byte na nakuha
			playerData=new String(buf);

			//itrim mo kasi baka may space na sumobra
			playerData = playerData.trim();
		

			//switch case depende sa anong stage na ung bukas nung game
			switch(gameStage){
				  case WAITING_FOR_PLAYERS: //habang nag-iintay pa ng players
						if (playerData.startsWith("CONNECT")){ //eto ung unang message na isesend from player (ung word na "CONNECT" saka ung name niya)
							String tokens[] = playerData.split(" "); //issplit mo ung nareceive na <"CONNECT" playername>

							//gagawa ka ng bagong player na ang data na kelangan ay <name ADDRESS PORT>
							//method ng DatagramPacket ung .getAddress() saka .getPort()
							NetPlayer player=new NetPlayer(tokens[1],packet.getAddress(),packet.getPort()); 

							//ipprint mo sa layout na connected na si player <Name>
							System.out.println("Player connected: "+tokens[1]);

							//eedit mo ung state ng game, dadagdag mo ung newly added player sa hashmap sa GameState.java
							game.update(tokens[1].trim(),player);

							//magsesend ka ng message to all players
							broadcast("CONNECTED "+tokens[1]);

							//iincrease ung count ng playercount
							playerCount++;
							if (playerCount==numPlayers){ //ccheck mo kung ung bilang ng players equal naba dun sa total na kelangan
								gameStage=GAME_START; //if oo, G na ung Game
							}
						}
					  break;	
				  case GAME_START: //kapag equal na dun sa kelangan ung players
					  System.out.println("Game State: START"); //print mo yaaas start na
					  broadcast("START"); //broadcast dun sa iba na START na!
					  gameStage=IN_PROGRESS; //change na ung state sa ONGOING
					  break;
				  case IN_PROGRESS: //kapag nagsstart na ung game
					  if (playerData.startsWith("PLAYER")){ //kapag ung sinend na data ay nagsstart sa "PLAYER" na keyword

						  //The format: PLAYER <player name> <x> <y>
						  //iisplit ung data para magamit					  
						  String[] playerInfo = playerData.split(" ");
						  String pname =playerInfo[1]; //eto ung para sa name
						  int x = Integer.parseInt(playerInfo[2].trim()); //eto ung X coordinate
						  int y = Integer.parseInt(playerInfo[3].trim()); //eto ung Y coordinate

						  //kukunin mo ung list ng players from game state, tapos iaccess mo ung merong key na equal dun sa current player
						  NetPlayer player=(NetPlayer)game.getPlayers().get(pname);		  
						  player.setX(x); //uuupdate mo ung x coordinate
						  player.setY(y); //uupdate mo ung Y coordinate
						  game.update(pname, player); //tapos iuupdate mo ung mismong value ng player na may key equal dun sa name nung current player
						  broadcast(game.toString()); //ibbroadcast mo sa lahat ng players yung nangyari
					  }
					  break;
			}				  
		}
	}	
	
	//pangbroadcast ng mga data players
	public void broadcast(String msg){

		//iikot siya sa lahat ng players dun sa hashmap
		for(Iterator ite=game.getPlayers().keySet().iterator();ite.hasNext();){
			String name=(String)ite.next(); //kukunin mo ung key (name) ng current iteration
			NetPlayer player=(NetPlayer)game.getPlayers().get(name); //gamit ung key (name), kukunin mo ung data nung player
			send(player,msg); //isesend sa lahat
		}
	}

	//pangsend
	public void send(NetPlayer player, String msg){
		DatagramPacket packet; //dedeclare ka ng packet
		byte buf[] = msg.getBytes(); //ung message mo icoconvert mo sa bytes since yun yung required

		//gagawa ka ng packet na ang parameters ay <msg, msgLength, address, portNumber>
		packet = new DatagramPacket(buf, buf.length, player.getAddress(),player.getPort()); 

		//sesend mo na ung data
		try{
			serverSocket.send(packet);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

}

