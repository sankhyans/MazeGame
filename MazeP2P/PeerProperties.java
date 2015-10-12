import java.util.*;
import java.io.Serializable;

public class PeerProperties implements Serializable 
{
	//To-Do - INfo about Primary and secondary servers
	static int primaryID;
	int primaryPort;
	static int secondaryID;
	int secondaryPort;
	long initTime;
	boolean gameStart;
	boolean joinFlag=true;
	public boolean isPrimaryServer;
	public boolean isBackupServer;
	public HashMap<String,String> primaryServer = new HashMap<String,String>();
	public HashMap<String,String> backupServer = new HashMap<String,String>();
	public HashMap<Integer, MazeGameP2PInterface> playerServerInfo = new HashMap<Integer, MazeGameP2PInterface>(); // <client_id,  mazeP2P object>

	public void setInitTime(long time)
	{
		initTime = time;
	}

	public long getInitTime()
	{
		return initTime;
	}

	public void gameStarted(boolean game)
	{
		gameStart=game;
	}

	public boolean hasGameStarted()
	{
		return gameStart;
	}

	public static void setPrimaryServer(int player_id)
	{
		primaryID = player_id;
	}

	public static int getPrimaryServer()
	{
		return primaryID;
	}

	public static void setBackupServer(int player_id)
	{
		secondaryID = player_id;
	}

	public static int getBackupServer()
	{
		return secondaryID;
	}

	public void setPrimaryPort(int port)
	{
		primaryPort = port;
	}

	public int getPrimaryPort()
	{
		return primaryPort;
	}

	public void setBackupPort(int port)
	{
		secondaryPort = port;
	}

	public int getBackupPort()
	{
		return secondaryPort;
	}

	public void isBackupServer(boolean ans)
	{
		isBackupServer = ans;
	}

	public void isPrimaryServer(boolean ans)
	{
		isPrimaryServer = ans;
	}
	public void canJoin(boolean flag)
	{
		joinFlag = flag;
	}

	public boolean canJoinFlag()
	{
		return joinFlag;
	}
}