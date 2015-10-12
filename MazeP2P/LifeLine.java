import java.rmi.RemoteException;

public class LifeLine implements Runnable{
	
	int playersID;
	
	@Override
	public void run() {
		
		while(true)
		{
			try
			{
				boolean status = MazeGameP2P.clientlifeStub.updateLifeStatus(playersID);
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
			catch(java.lang.NullPointerException exp)
			{
				//System.out.println("primary server has died. Waiting for secondary to come up !");
			}
		}
	}

	public void setPlayerID(Integer client_id)
	{
		playersID = client_id;
	}
	
}