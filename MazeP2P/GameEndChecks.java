import java.rmi.RemoteException;
import java.util.*;

public class GameEndChecks implements GameEndChecker
{
	MazeGame game;
	int remainingTreasure=0;
	int mazeSize=0;
	public void setState(MazeGame gameState, int mazeSize_)
	{
		game=gameState;
		mazeSize = mazeSize_;
	}

	public MazeGame gameEndInfo() throws RemoteException
	{

		try
    {
    	remainingTreasure=game.getRemainingTreasures();
    	
    	if(0==remainingTreasure)
    	{
        System.out.println("All the treasures have been allocated. Ending the game.");
    		return game;
    	}
        
        if(game.playerActiveTime.isEmpty())
        {
            System.out.println("All the players have died. Ending the game.");
            return game;
        }
        return null;
      }
      catch(Exception exp)
      {
      	System.out.println("endGame");
      	exp.printStackTrace();
        System.exit(0);
      }
      return null;
   		
	}
}