import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.util.*;
	
public class MazeGameServer extends UnicastRemoteObject implements MazeGameMethods {

	private boolean joinFlag;
	private int joinTime;
	private boolean maxPlayers = true;

	private static MazeGame mazeGame;
	public static int mazeSize;
	public static int treasureCount;
	public int remainingTreasure;
    public int activePlayerCount=0;
    private static GameState gameState;

    public MazeGameServer() throws RemoteException {

    	super();

    	//In the starting of the game the players are allowed to join
    	//joinFlag = false means the server will not all any more join requests
    	//joinTime specifies the time (in secs) for which the players are allowed
    	//to join the game
    	joinFlag = true;
    	joinTime = 20;
        remainingTreasure = treasureCount;

    	//We will start the server's join timer to process join requests
    	startJoinTimer();
    }

    private void startJoinTimer() {
    	Timer joinTimer = new Timer();
    	joinTimer.schedule(new StopJoinRequests(),joinTime*1000);
	}

	class StopJoinRequests extends TimerTask {

		public void run() {

			// This function runs when the joinTimer has timed out!
			// In our case that's 20 secs after the game started
			// Now we should shut the joinFlag (set it to false)
			// and launch the game! :)

			joinFlag = false;
			try
            {
				launchGame();
			} catch (RemoteException exp) {
	    		System.err.println("launchGame RemoteException: " + exp.toString());
				exp.printStackTrace();
				System.exit(1);
    		} catch (Exception exp) {
    			System.err.println("launchGame Exception: " + exp.toString());
				exp.printStackTrace();
				System.exit(1);
    		}
		} 
    }


    // This is the remote method
    // which can be invoked by the client
    // when they wish to join the game!
	public boolean joinGame(ClientConnect client, int client_id) throws RemoteException
	{

	    if((mazeGame.playerList.size() < mazeGame.maxPlayerCount) && (joinFlag))
	    {
	   		mazeGame.playerList.put(client,client_id);
	   		mazeGame.allocatePlayerLocation(client_id);
            mazeGame.playerActiveTime.put(client_id,System.currentTimeMillis());
            activePlayerCount++;
            updateGameState();
	   		System.out.println("New player joined: " + client_id);

	   		client.message("you are in! The game will start soon...","INFO");
	   		client.message(String.valueOf(mazeSize),"MAZE_SIZE");

	  		return true;
	  	}
	   	else
	   	{
    		client.message("Max No. of Players reached or Can not accept more requests.","INFO");
    		return false;
	    }
	}


    private void launchGame() throws RemoteException
    {
    	
    	System.out.println("Namaste!");
    	System.out.println(" The game begins!! with a maze of size "+mazeSize+" and "+treasureCount+" treasures!");
        updateGameState();
        int client_id=0;
    	// Inform every player that the game has started!
    	try
    	{
			for(Map.Entry<ClientConnect,Integer> player : mazeGame.playerList.entrySet()) {
    			ClientConnect playerClient = player.getKey();
                client_id=player.getValue();
    			playerClient.message("The game begins " + player.getValue() + " !","INFO");
    			playerClient.message("","SOG");
                continue;
    		}
            remainingTreasure=mazeGame.remainingTreasures();
            if(0==remainingTreasure)
            {
                System.out.println("zero");
                System.exit(0);
            }
    	}
        catch(java.rmi.ConnectException exp)
        {
            System.out.println("Player "+client_id+" has died.");
            gameState.clearPlayer(client_id);
            updateGameState();
        }
    	catch (java.rmi.UnmarshalException exp)
    	{
    		System.out.println("All the players have died. Exiting the game ! Dhanayvaad ");
    		System.exit(0);
    	}
    }

    public void updateGameState()
    {
        gameState.mazePlayerState = mazeGame.mazePlayerState;
        gameState.mazeTreasureState = mazeGame.mazeTreasureState;
        gameState.mazePlayerTreasures = mazeGame.mazePlayerTreasures;
        gameState.playerActiveTime = mazeGame.playerActiveTime;
        gameState.playerList = mazeGame.playerList;
    }

    public void updateMazeGameList()
    {
        mazeGame.mazePlayerState = gameState.mazePlayerState;
        mazeGame.mazeTreasureState = gameState.mazeTreasureState;
        mazeGame.mazePlayerTreasures = gameState.mazePlayerTreasures;
        mazeGame.playerActiveTime = gameState.playerActiveTime;
        mazeGame.playerList = gameState.playerList;
    }


    // This function is invoked by the client for the following information:
	// Location of all the players in maze
	// Location of all the treasures in maze
	// count of newly collected treasures (if any)
	// to all the clients.
    public synchronized GameState getState() throws RemoteException {
    	
    	updateGameState();
    	return gameState;
    }

    public synchronized GameState getState(String move, int client_id) throws RemoteException
    {  
        updateMazeGameList();
        //update the last active time
        //mazeGame.updateLastActiveTime(client_id);
    	
    	//Processing the move
        if(move.equals("kill") || move.equals("quit"))
        {
            for(Map.Entry<ClientConnect,Integer> player : mazeGame.playerList.entrySet())
            {
                if(player.getValue() == client_id)
                {
                    ClientConnect client = player.getKey();
                    if(gameState.clearPlayer(client_id))
                    {
                      System.out.println("Player "+client_id+" has killed himself. Removed him from the player list.");
                    }
                    else
                    {
                        System.out.println("Unable to remove Player "+client_id+" from the player list.");
                    }
                    updateGameState();
                    client.message("Dhanayvaad","EOG");
                }
            }
        }
        else if(!mazeGame.move(move,client_id))
        {
			for(Map.Entry<ClientConnect,Integer> player : mazeGame.playerList.entrySet())
            {
	    		if(player.getValue() == client_id)
                {
	    			ClientConnect client = player.getKey();
    				client.message("Invalid move!","INFO");
    			}
	    	}	    	
    	}
        updateGameState();
        // check if all the treasures have been allocated
        endGame();
    	return gameState;
    }


    public synchronized void endGame() throws RemoteException
    {
        //If all the treasures have been allocated
      try
      {
    	remainingTreasure=mazeGame.remainingTreasures();
    	
    	if(0==remainingTreasure)
    	{
    		System.out.println("All the treasures have been allocated. Ending the game.");
    		for(Map.Entry<ClientConnect,Integer> player : mazeGame.playerList.entrySet())
             {
                ClientConnect client = player.getKey();
                client.message("All the treasures have been allocated. Ending the game.","EOG");
            }
            System.exit(0);
    	}
        int playerCount=0;
        for (int x =0;x<mazeSize;x++)
        {
            for ( int y=0;y<mazeSize;y++)
            {
                if((mazeGame.mazePlayerState[x][y]) !=0)
                {
                    playerCount++;
                }
            }
        }
        activePlayerCount=playerCount;
        if(0==activePlayerCount)
        {
            System.out.println("All the treasures have died. Ending the game.");
            System.exit(0);
        }
      }
      catch(java.rmi.UnmarshalException exp)
      {
        System.exit(0);
      }
   		
    }

	public static void main(String args[]) throws RemoteException{

		// Initiate the mazeGameServer
		// and register it with the RMIService
		MazeGameServer mazeGameServer = new MazeGameServer();
    
        gameState = new GameState();

		String registrationUrl = "rmi://127.0.0.1:1099";
		Registry registry = null;
		MazeGameMethods serverStub = null;
        ClientLifeChecker lifeStub = null;
        
        ClientLifeChecks clientLifeChecks = new ClientLifeChecks();
        clientLifeChecks.setState(gameState);
        
        ClientLife clientLife = new ClientLife();
        clientLife.setState(gameState);
        Thread clientLifeChecker = new Thread(clientLife); 

		try {

			try {

				serverStub = (MazeGameMethods) UnicastRemoteObject.exportObject(mazeGameServer, 0);
                lifeStub = (ClientLifeChecker) UnicastRemoteObject.exportObject(clientLifeChecks, 0);
			    registry = LocateRegistry.getRegistry(registrationUrl);
			    registry.rebind("server", serverStub);
                registry.rebind("ClientLife",lifeStub);
			}

			catch (ExportException exp) {

				UnicastRemoteObject.unexportObject(mazeGameServer, true);
				serverStub = (MazeGameMethods) UnicastRemoteObject.exportObject(mazeGameServer, 0);
                lifeStub = (ClientLifeChecker) UnicastRemoteObject.exportObject(clientLifeChecks, 0);
			    registry = LocateRegistry.getRegistry();
			    registry.rebind("server",serverStub);			
			     registry.rebind("ClientLife",lifeStub);
            }

		    // Ailaan!
			System.out.println("Game join begins!");

			// Get the maze size and the treasure count from
			// Command line input
			mazeSize = Integer.parseInt(args[0]);
    		treasureCount = Integer.parseInt(args[1]);

    		// Instantiate the mazeGame
        	mazeGame = new MazeGame(mazeSize,treasureCount);

            // Start the thread to check remaining clients 
            clientLifeChecker.start();
		}
		catch (ArrayIndexOutOfBoundsException exp)
		{
			System.err.println("MazeGameServer Exception: " + exp.toString());
			exp.printStackTrace();
			System.out.println("Correct way of using :");
			System.out.println("java MazeGameServer <Maze Size> <Maximum Treasure>");
			System.exit(1);
		}
		catch (Exception exp) {

			System.err.println("MazeGameServer Exception: " + exp.toString());
			exp.printStackTrace();
			System.exit(1);

		}

	}
    
}
