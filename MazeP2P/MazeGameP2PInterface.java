import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MazeGameP2PInterface extends Remote
{
	//final static Integer START_WAITING_TIME = 20;
	final static Integer INJECTED_DELAY = 3;
	final static Integer PLAYER_MOVE_MAX_DELAY = 3;
	final static Integer GAME_START_DELAY = 2;

	public void initClientLifeThread() throws RemoteException;
	public void initGameEndThread() throws RemoteException;
	public boolean joinGame(int client_id) throws RemoteException;
    public void playGame() throws RemoteException;
    public PeerProperties GetPeerProperties() throws RemoteException;

    public void setActivePlayerCount(int count) throws RemoteException;
    public int getActivePlayerCount() throws RemoteException;
    public int getMaxPlayers() throws RemoteException;
    public boolean canJoin() throws RemoteException;
    public int getMazeSize() throws RemoteException;
}