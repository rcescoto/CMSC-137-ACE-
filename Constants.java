//lists lang siya ng mga constant stuffs
public interface Constants {
	//eto yung name nung app
	public static final String APP_NAME="Circle Wars 0.01";
	
	//eto ung states na possible na meron yung game
	public final int WAITING_FOR_PLAYERS=3; //habang nag-aantay pa ng players
	public static final int GAME_START=0; //kapag nakakuha na ng enough players
	public static final int IN_PROGRESS=1; //kapag game na
	public final int GAME_END=2; //kapag tapos na yung game
	
	//default port number
	public static final int PORT=4444; 
}
