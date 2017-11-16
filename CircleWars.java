import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JFrame;
import javax.swing.JPanel;

//client thread, eto ung mismong game
public class CircleWars extends JPanel implements Runnable, Constants{
	
	//gagawa ka ng frame
	JFrame frame= new JFrame();
	int x=10,y=10,xspeed=2,yspeed=2,prevX,prevY; //default values for coordinates and speed ng movement
	Thread t=new Thread(this);
	String name="Joseph";
	String pname;
	String server="localhost";
	boolean connected=false;
	DatagramSocket socket = new DatagramSocket(); //gawa ka ng datagram socket
	String serverData; //storage ng mga data galing server
	BufferedImage offscreen; //buffered image - for pixel data

	//main part - dedeclaran ka lang nya ng error kapag mali ung input mong pinasa - kelangan mo ng server saka name
	public static void main(String args[]) throws Exception{
		if (args.length != 2){
			System.out.println("Usage: java -jar circlewars-client <server> <player name>");
			System.exit(1);
		}

		//gagawa ka ng bagong game using ung server at name value
		new CircleWars(args[0],args[1]);
	}

	public CircleWars(String server,String name) throws Exception{
		this.server=server; //assign mo ung server sa server
		this.name=name; //name sa name
		
		frame.setTitle(APP_NAME+":"+name); //gagawa ka frame na may title mula dun sa constants
		socket.setSoTimeout(100); //set ka ng timeout
		
		//pangsetup dun sa mismong panel nung game
		frame.getContentPane().add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setVisible(true);
		
		//gawa ka ng image na same dun sa size nung frame
		offscreen=(BufferedImage)this.createImage(640, 480);
		
		//ung frame lagyan mo ng key at mouse handler
		frame.addKeyListener(new KeyHandler());		
		frame.addMouseMotionListener(new MouseMotionHandler());

		//start mo na ung thread!
		t.start();		
	}
	
	public void run(){
		while(true){
			try{
				Thread.sleep(1); //sleep ung thread para makapagreload? charge? idk? HAHAHA basta patulugin mo!
			}catch(Exception ioe){}
						
			//declare ka ng bagong byte shits
			byte[] buf = new byte[256];

			//create ka ng datagrampacket for receiving data params lang ay <MSG na lagayan, LengthMsg>
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
     			socket.receive(packet); //receive mo na packet boi
			}catch(Exception ioe){/*lazy exception handling :)*/}
			
			//convert mo ung data to string (kasi galing siyang byte)
			serverData=new String(buf);
			serverData=serverData.trim(); //trim mo kasi baka masamang weirdo

			
			//kapag hindi pa connected pero "CONNECTED" using sinend nung server para maconnect na siya
			if (!connected && serverData.startsWith("CONNECTED")){
				connected=true; //icoconnect mo na yay
				System.out.println("Connected.");
			}else if (!connected){ //eto ung legit na unang mababasa kasi ung player ung unang magsesend ng data to server
				System.out.println("Connecting..");				
				send("CONNECT "+name); //send niya yung "CONNECT" (pangdetect) plus name niya
			}else if (connected){ //pagconnected na siya
				offscreen.getGraphics().clearRect(0, 0, 640, 480); //gagawa lang siya ng panel na blank
				if (serverData.startsWith("PLAYER")){ //kapag PLAYER ung nadetect - may nasend ng data from server
					String[] playersInfo = serverData.split(":"); //kukunin niya yung mga info ng players tapos issplit niya (kasi un ung pangsplit based sa server)
					for (int i=0;i<playersInfo.length;i++){ //loop ka sa lahat ng data
						String[] playerInfo = playersInfo[i].split(" "); //split mo ung data
						String pname =playerInfo[1]; //kunin mo name
						int x = Integer.parseInt(playerInfo[2]); //kunin mo x coordinate
						int y = Integer.parseInt(playerInfo[3]); //kunin mo y coordinate

						//draw on the offscreen image
						offscreen.getGraphics().fillOval(x, y, 20, 20);
						offscreen.getGraphics().drawString(pname,x-10,y+30);					
					}

					//repaint mo ung frame boi
					frame.repaint();
				}			
			}			
		}
	}

	//pangsend ng data, same nung sa server
	public void send(String msg){
		try{
			//convert mo una ung message to bytes
			byte[] buf = msg.getBytes();

			//kunin mo ung address based sa server na pinasa
        	InetAddress address = InetAddress.getByName(server);

        	//gawa ka ng datagram packet na may params na <ByteMsg, MsgLength, Address, Port)
        	DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);

        	//send mo na sa datagramsocket ung packet
        	socket.send(packet);
        }catch(Exception e){}
		
	}
	
	//pangdraw ng image dun sa bilog
	public void paintComponent(Graphics g){
		g.drawImage(offscreen, 0, 0, null);
	}

	//pagnagmove ung mouse, kunin mo ung x nd y coordinate nung movement tapos isend mo sa server dapat ang params ay
	//PLAYER playerName xCoordinate yCoordinate (yung PLAYER ay para lang marecognize nya na eto ung certain data na yon)
	class MouseMotionHandler extends MouseMotionAdapter{
		public void mouseMoved(MouseEvent me){
			x=me.getX();y=me.getY();
			if (prevX != x || prevY != y){
				send("PLAYER "+name+" "+x+" "+y);
			}				
		}
	}

	//pangnagmoveung keys na kelangan, kunin mo ung x and y coordinate nung movement tapos isend mo sa server
	//PLAYER playerName xCoordinate yCoordinate (yung PLAYER ay para lang marecognize nya na eto ung certain data na yon)
	class KeyHandler extends KeyAdapter{
		public void keyPressed(KeyEvent ke){
			prevX=x;prevY=y;
			switch (ke.getKeyCode()){
			case KeyEvent.VK_DOWN:y+=yspeed;break;
			case KeyEvent.VK_UP:y-=yspeed;break;
			case KeyEvent.VK_LEFT:x-=xspeed;break;
			case KeyEvent.VK_RIGHT:x+=xspeed;break;
			}
			if (prevX != x || prevY != y){
				send("PLAYER "+name+" "+x+" "+y);
			}	
		}
	}
	
	
}
