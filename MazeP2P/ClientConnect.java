import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientConnect extends Remote {
	
	public void message(String msg, String type) throws RemoteException;

}
