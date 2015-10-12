import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientLifeChecker extends Remote{
	public boolean updateLifeStatus(Integer id) throws RemoteException;
}