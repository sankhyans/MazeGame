import java.util.*;

public class GameEnd implements Runnable {

	MazeGame game ;
	GameEndChecker endState;

	@Override
	public void run()
	{
		while (true)
		{
		try
		{
			if(MazeGameP2P.endGameStub !=null)
			{
				game=MazeGameP2P.endGameStub.gameEndInfo();
			if(game==null)
			{
				Thread.sleep(5000);
			}
			else
			{
				System.out.println("Game is over !");
				System.exit(0);
			}
		}
		}
		catch (java.util.ConcurrentModificationException exp)
		{
			System.out.println("\n");
		}
		catch (InterruptedException ie) {
		}
		catch(java.rmi.ConnectException exp)
		{
			System.out.println("Primary server is not available.");
			int id=PeerProperties.getBackupServer();
			PeerProperties.setPrimaryServer(id);
				
		}
		catch(java.lang.ClassCastException e){
			System.exit(0);
		}
		catch(Exception exp)
		{
			System.out.println("Thread Exception raised by GameEnd");
			exp.printStackTrace();
            System.exit(0);
		}
		}
	}
}