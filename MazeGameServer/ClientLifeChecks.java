import java.rmi.RemoteException;
import java.util.*;

public class ClientLifeChecks implements ClientLifeChecker
{
	GameState gameState;
	public void setState(GameState gameState)
	{
		this.gameState=gameState;
	}

	public boolean updateLifeStatus(Integer id) throws RemoteException
	{
		for(Map.Entry<Integer,Long> player : gameState.playerActiveTime.entrySet())
        {
        	int pid=player.getKey();
            if(pid == id)
            {
               gameState.playerActiveTime.put(id,System.currentTimeMillis());
               return true;
            }
        }
        return false;
	}
}