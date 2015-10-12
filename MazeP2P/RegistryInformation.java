import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryInformation
{
	static Registry registry;
	static Registry primaryRegistry;
	int primaryPort;

	public static Registry startRegistry(int port)
	{
		try
		{
			//int p_ip = Integer.parseInt(ip);
			//primaryPort = port;
			registry = LocateRegistry.createRegistry(port);
		}
		catch(Exception exp)
		{
			System.out.println("Failed to start registry.");
			return null;
		}
		return registry;
	}

	public static Registry getRegistry()
	{
		return registry;
	}

	public static void setPrimaryRegistry(Registry registry)
	{
		primaryRegistry = registry;
	}

	public static Registry getPrimaryRegistry()
	{
		return primaryRegistry;
	}

	public static Registry setRegistryWithIP(String IP, int port)
	{
		try
		{
			String ip = IP;
			//registry = LocateRegistry.createRegistry(ip, port);
		}
		catch(Exception exp)
		{
			System.out.println("Failed to start registry.");
			return null;
		}
		return registry;
	}

	/*public static MazeP2PInterface getServerStub(String stubType)
	{
		Registry registrySub = LocateRegistry.getRegistry("127.0.0.1",primaryPort);
        return (MazeP2PInterface) registrySub.lookup(stubType);
        //return mazeStub;
	}*/
}