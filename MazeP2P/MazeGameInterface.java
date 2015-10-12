import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface MazeGameInterface extends Remote
{
	public boolean move(String moveName, int client_id) throws RemoteException;
    public boolean getState(String move, int client_id) throws RemoteException;
     public int getMazeSize() throws RemoteException;
    public int getTreasureCount() throws RemoteException;
    
    public int[][] getMazePlayerState() throws RemoteException;
    public int[][] getMazeTreasureState() throws RemoteException;
    public HashMap<Integer,Integer> getMazePlayerTreasures() throws RemoteException;
    public HashMap<Integer,Long> getPlayerActiveTime() throws RemoteException;
}