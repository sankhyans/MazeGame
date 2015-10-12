import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MazeGameMethods extends Remote {
	
	public boolean joinGame(ClientConnect client, int client_id) throws RemoteException;
    public GameState getState() throws RemoteException;
    public GameState getState(String move, int client_id) throws RemoteException;
}
