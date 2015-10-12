import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.io.Serializable;

public class GameStateP2P implements Serializable {


 	public int[][] mazePlayerState;
	public int[][] mazeTreasureState;
	public HashMap<Integer,Integer> mazePlayerTreasures = new HashMap<Integer,Integer>(); //<client_id,client_treasures>
	public HashMap<Integer,Long> playerActiveTime = new HashMap<Integer,Long>(); //<client_id,client_active time>
	public HashMap<MazeGameP2PInterface,Integer> playerList = new HashMap<MazeGameP2PInterface,Integer>(); //<Client object,client_id>

	public GameStateP2P() {
		super();
	}

	public synchronized 
	public synchronized boolean clearPlayer(int client_id)
    {
        int numRows = mazePlayerState.length;
        int x = 0;
        int y = 0;
        for(x=0;x<numRows;x++){
            int[] columnArray = mazePlayerState[x];
            int numColumns = columnArray.length;
            for(y=0;y<numColumns;y++){
                if(mazePlayerState[x][y] == client_id)
                {
                	mazePlayerState[x][y]=0;
                }
            }
        }
        return true;
    }
}