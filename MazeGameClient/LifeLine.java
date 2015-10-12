import java.rmi.RemoteException;

public class LifeLine implements Runnable{
	
	ClientLifeChecker clientLife;
	int playersID;
	
	@Override
	public void run() {
		
		while(true)
		{
			try
			{
				boolean status = MazeGameClient.clientlifeStub.updateLifeStatus(playersID);
				if(!status)
				{
					System.out.println("Failed to send the updated status to server.");
				}
				Thread.sleep(10000);
			}
			catch (InterruptedException exp) { 
			}
			catch (RemoteException exp){
			}
		}
	}

	public void setPlayerID(Integer client_id)
	{
		playersID = client_id;
	}
	public void setStub(ClientLifeChecker clientlifeStub)
	{
		clientLife = clientlifeStub;
	}
}