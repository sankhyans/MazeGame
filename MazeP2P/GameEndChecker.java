import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameEndChecker extends Remote{
	public MazeGame gameEndInfo() throws RemoteException;
}