import java.util.*;

public class ClientLife implements Runnable {

	GameState game ;

	public void setState(GameState gameState)
	{
		game = gameState;
	}

	@Override
	public void run()
	{
		while (true)
		{
		try
		{
			Thread.sleep(5000);
		  	synchronized(game.playerActiveTime)
			{
				for (Map.Entry<Integer,Long> player : game.playerActiveTime.entrySet())
				{
					Integer client_id = player.getKey();
					long lastActiveTime= player.getValue();
					long curTime = System.currentTimeMillis();
					if (lastActiveTime != 0)
					{
						if (curTime - lastActiveTime > 10000)
						{
							System.out.println("Player " + client_id + " has died.");
							game.clearPlayer(client_id);
        					synchronized(game.playerActiveTime)
       	 					{
            					game.playerActiveTime.remove(client_id);
        					}
        					if(game.playerActiveTime.isEmpty())
        					{
        						System.out.println("All the players have died. Quitting Game");
        						System.exit(0);
        					}
						
						}
					}
				}
			}
		}
		catch (java.util.ConcurrentModificationException exp)
		{
			System.out.println("\n");
		}
		catch (InterruptedException ie) {
		}
		}
	}
}