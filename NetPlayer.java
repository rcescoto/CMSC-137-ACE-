import java.net.InetAddress;

//Eto ung mga information ng isang player, nandito ung kelangna mo sakanya
public class NetPlayer {
	private InetAddress address; //address
	private int port; //port
	private String name; //name
	private int x,y; //coordinates

	//gagawa ng netplayer based sa address,port,name na pinasa
	public NetPlayer(String name,InetAddress address, int port){
		this.address = address;
		this.port = port;
		this.name = name;
	}

	//pangreturn ng address value
	public InetAddress getAddress(){
		return address;
	}

	//pangreturn ng port value
	public int getPort(){
		return port;
	}

	//pangreturn ng name
	public String getName(){
		return name;
	}
	
	//pangset ng X coordinate
	public void setX(int x){
		this.x=x;
	}
	
	//pangget ng X coordinate
	public int getX(){
		return x;
	}
	
	//pangget ng Y coordinate
	public int getY(){
		return y;
	}
	
	//pangset ng Y coordinate
	public void setY(int y){
		this.y=y;		
	}

	//coconvert mo ung data ng player to string
	public String toString(){
		String retval="";
		retval+="PLAYER ";
		retval+=name+" ";
		retval+=x+" ";
		retval+=y;
		return retval;
	}	
}
