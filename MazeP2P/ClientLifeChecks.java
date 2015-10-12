import java.rmi.RemoteException;
import java.util.*;

public class ClientLifeChecks implements ClientLifeChecker
{
	MazeGame game;
	public void setState(MazeGame gameState)
	{
		game=gameState;
	}

	public boolean updateLifeStatus(Integer id) throws RemoteException
	{
		for(Map.Entry<Integer,Long> player : game.playerActiveTime.entrySet())
        {
        	int pid=player.getKey();
            if(pid == id)
            {
               game.playerActiveTime.put(id,System.currentTimeMillis());
               return true;
            }
        }
        return false;
	}
}