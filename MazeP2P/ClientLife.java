import java.util.*;

public class ClientLife implements Runnable {

	MazeGame game ;
	Integer client_id=0;

	public void setState(MazeGame mazeGame)
	{
		game = mazeGame;
	}

	@Override
	public void run()
	{
		try{
		while (true)
		{
		try
		{
			Thread.sleep(5000);
		  	synchronized(game.playerActiveTime)
			{
				for (Map.Entry<Integer,Long> player : game.playerActiveTime.entrySet())
				{
					client_id = player.getKey();
					long lastActiveTime= player.getValue();
					long curTime = System.currentTimeMillis();
					if (lastActiveTime != 0)
					{
						if ((curTime - lastActiveTime > 25000) && (client_id != PeerProperties.getPrimaryServer()))
						{
							System.out.println("Player " + client_id + " has died.");
							game.clearPlayer(client_id);
        					synchronized(game.playerActiveTime)
       	 					{
            					game.playerActiveTime.remove(client_id);
        					}
						}
						if(game.playerActiveTime.isEmpty())
        				{
        					System.out.println("All the players have died. Quitting Game");
        					System.exit(0);
        				}
        				else if((game.playerActiveTime.size() == 1) && (client_id == PeerProperties.getPrimaryServer()))
        				{
        					Thread.sleep(5000);
        					System.out.println("All the players have died. Quitting Game");
        					System.exit(0);
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
		catch(Exception exp)
		{
			if(client_id==0)
			{
				System.out.println("Thread Exception raised in ClientLife");
			}
			else
			{
				System.out.println("Thread Exception raised by "+client_id + " in ClientLife");
			}
		}
		}
	}
	catch(Exception e)
	{}
}
}