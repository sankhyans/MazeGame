import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

public class MazeGameP2P extends UnicastRemoteObject implements MazeGameP2PInterface,Serializable
{
	private boolean joinFlag;
	private int joinTime;
	private boolean maxPlayers = true;
	public boolean isPrimaryServer=false;

	private static MazeGame mazeGame;
    public static MazeGameP2P p2pplayer;
	public int mazeSize;
	public int treasureCount;
	public int remainingTreasure;
    public int activePlayerCount=0;
    public int player_id;
    public String playerName;
    public static MazeGameP2PInterface clientStub = null;
    public static ClientLifeChecker clientlifeStub = null;
    public static GameEndChecker endGameStub=null;
	public HashMap<Integer,String> playerDetails = new HashMap<Integer,String>(); //player ID, player Name

    //New variables for P2P implementation
    static PeerProperties peerProperty = new PeerProperties();
    public int maxPlayerCount=0;
    public static  MazeGameInterface mazeGStub = null;
    boolean startGame=false;

    public MazeGameP2P() throws RemoteException {

    	super();

    	//In the starting of the game the players are allowed to join
    	//joinFlag = false means the server will not all any more join requests
    	//joinTime specifies the time (in secs) for which the players are allowed
    	//to join the game
    	joinFlag = true;
    	joinTime = 20;
    }

    public void setActivePlayerCount(int count) throws RemoteException
    {
        activePlayerCount=count;
    }
    public int getActivePlayerCount() throws RemoteException
    {
        return activePlayerCount;
    }

    public int getMaxPlayers() throws RemoteException
    {
        if (mazeSize%2 == 0)
        {
            maxPlayerCount = (mazeSize*mazeSize)/2;
        }
        else
        {
            maxPlayerCount = ((mazeSize*mazeSize)-1)/2;
        }
        return maxPlayerCount;
    }
    public boolean canJoin() throws RemoteException
    {
        return joinFlag;
    }
    public int getMazeSize() throws RemoteException
    {
        return mazeSize;
    }
    public void setMazeSize(int size) throws RemoteException
    {
        mazeSize = size;
    }
    public void setCanJoin(boolean flag)
    {
        joinFlag=flag;
    }

 // This is the remote method
    // which can be invoked by the client
    // when they wish to join the game!
    public synchronized boolean joinGame(int client_id) throws RemoteException
    {
            mazeGame.playerActiveTime.put(client_id,System.currentTimeMillis());
            mazeGame.allocatePlayerLocation(client_id);
            setActivePlayerCount(activePlayerCount++);
            activePlayerCount++;
            System.out.println("New player joined: " + client_id);
            return true;
    }

    public void startJoinTimer()
    {
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
            setCanJoin(false);
            peerProperty.gameStarted(true);
            System.out.println("Namaste!");
            System.out.println(" The game begins!! with a maze of size "+mazeSize+" and "+treasureCount+" treasures!");
        } 
    }
    //Main function
	public static void main(String args[]) throws RemoteException
	{
		if(args.length < 1)
		{
			System.out.println("Correct way of using :");
			System.out.println("For the 1st user:");
			System.out.println("java MazeGameP2P <playerName> <Maze Size> <Maximum Treasure> [ipAddress]");
			System.out.println("For joining the already initiated game:");
			System.out.println("java MazeGameP2P <PlayerName>");
			System.exit(0);
		}
		MazeGameP2P mazep2pPlayer = new MazeGameP2P();
		mazep2pPlayer.init(args);

     }

     public void init(String[] main_args) throws RemoteException
     {
     	try{
            p2pplayer = new MazeGameP2P();
		Random gen = new Random();
        mazeGame = new MazeGame();
    	player_id = gen.nextInt(Integer.MAX_VALUE) + 1;
     	if((main_args[0]).equals("1st"))
        {
            mazeSize = Integer.parseInt(main_args[2]);
            treasureCount = Integer.parseInt(main_args[3]);
            setMazeSize(mazeSize);   
            mazeGame.setMazeSize(mazeSize);
            mazeGame.setTreasureCount(treasureCount);
            int i=startGameAsServer();
     	if(i == -1)
     	{
     		System.out.println("Can not start game. Exiting. Dhanayavaad");
     		System.exit(0);
     	}
        else if(i == 0)
        {
            System.out.println("Primary server is up !");
            mazeSize = Integer.parseInt(main_args[2]);
            treasureCount = Integer.parseInt(main_args[3]);
            setMazeSize(mazeSize);
            mazeGame.setMazeSize(mazeSize);
            mazeGame.setTreasureCount(treasureCount);
            mazeGame.init();
            
            //We will start the server's join timer to process join requests
            startJoinTimer();

            //Starting the threads for checking the client's life
        
            ClientLife clientLife = new ClientLife();
            clientLife.setState(mazeGame);
            Thread clientLifeChecker = new Thread(clientLife);
            clientLifeChecker.start();

        
            playerName = main_args[1];
            playerDetails.put(player_id,playerName);
            initClientLifeThread();
            initGameEndThread();
            playerDetails.put(player_id,playerName);
            if(!startGameAsPlayer())
            {
                System.out.println("Cannot join game. Exiting. Dhanayavaad");
                System.exit(0);
            }

            System.out.println("Namaste player "+playerName+"!");
            if(!peerProperty.canJoinFlag())
            {
                System.out.println("Cannot join game.");
                System.exit(0);
            }
            while(!peerProperty.hasGameStarted())
            {
                try
                {
                    peerProperty.canJoin(false);
                    Thread.sleep(3000);
                }
           catch (InterruptedException ie) {}
            }
        }}
        if(!isPrimaryServer)
        {
            playerName = main_args[0];
            playerDetails.put(player_id,playerName);
            if(!startGameAsPlayer())
     	    {
     			System.out.println("Cannot join game. Exiting. Dhanayavaad");
     			System.exit(0);
     		}

     		System.out.println("Namaste player "+playerName+"!");
        }

        
        playGame();
        }
     	catch(java.lang.ArrayIndexOutOfBoundsException exp)
     	{
     		System.out.println("Correct way of using :");
			System.out.println("For the 1st user:");
			System.out.println("java MazeGameP2P <playerName> <Maze Size> <Maximum Treasure> [ipAddress]");
			System.out.println("For joining the already initiated game:");
			System.out.println("java MazeGameP2P <PlayerName>");
            exp.printStackTrace();
			System.exit(0);
     	}
     	catch(Exception exp)
     	{
     		System.out.println("init exception" + exp.toString());
     		exp.printStackTrace();
     	}
     }

     public void initClientLifeThread() throws RemoteException
     {
        LifeLine lifeLine = new LifeLine();
        Thread clientLifeThread = new Thread(lifeLine);
        lifeLine.setPlayerID(player_id);
        clientLifeThread.start();
     }

     public void initGameEndThread() throws RemoteException
     {
        GameEnd gameEnd = new GameEnd();
        Thread gameendThread = new Thread(gameEnd);
        gameendThread.start();
     }

     // To start the game as a primary server, backup server or as a player
     public int startGameAsServer() throws RemoteException
     {
     	try
     	{

            String registrationUrl = "rmi://127.0.0.1:1099";
			Registry registry = null;
            
            // Maze game P2P server stub
			MazeGameP2PInterface serverStub = null;

            //Client Life stub
       		ClientLifeChecker lifeStub = null;
            ClientLifeChecks clientLifeChecks = new ClientLifeChecks();
            clientLifeChecks.setState(mazeGame);

            //game end stub
            GameEndChecks gameEndChecks = new GameEndChecks();
            gameEndChecks.setState(mazeGame,mazeSize);
            GameEndChecker endStub = null;

            //game movement stub
            MazeGame mazeGame = new MazeGame();
            MazeGameInterface mazeStub =null;

			try
			{
				serverStub = (MazeGameP2PInterface) UnicastRemoteObject.exportObject(p2pplayer, 0);
                lifeStub = (ClientLifeChecker) UnicastRemoteObject.exportObject(clientLifeChecks, 0);
			    registry = LocateRegistry.getRegistry(registrationUrl);
                registry.bind("pserver", serverStub);
                registry.bind("ClientLife",lifeStub);
                endStub = (GameEndChecker) UnicastRemoteObject.exportObject(gameEndChecks,0);
                registry.bind("GameEnd",endStub);
                mazeStub = (MazeGameInterface) UnicastRemoteObject.exportObject(mazeGame,0);
                registry.bind("mazeMovement",mazeStub);
			}
			catch (ExportException exp)
			{
				UnicastRemoteObject.unexportObject(p2pplayer, true);
               // UnicastRemoteObject.unexportObject(clientLifeChecks, true);
                //UnicastRemoteObject.unexportObject(gameEndChecks, true);
                UnicastRemoteObject.unexportObject(mazeGame, true);
				serverStub = (MazeGameP2PInterface) UnicastRemoteObject.exportObject(p2pplayer, 0);
                lifeStub = (ClientLifeChecker) UnicastRemoteObject.exportObject(clientLifeChecks, 0);
			    registry = LocateRegistry.getRegistry();
                registry.rebind("pserver",serverStub);			
			    registry.rebind("ClientLife",lifeStub);
                endStub = (GameEndChecker) UnicastRemoteObject.exportObject(gameEndChecks,0);
                registry.rebind("GameEnd",endStub);
                mazeStub = (MazeGameInterface) UnicastRemoteObject.exportObject(mazeGame,0);
                registry.rebind("mazeMovement",mazeStub);
            }

            //Start the primary server
            peerProperty.setPrimaryServer(player_id);
            peerProperty.setPrimaryPort(1099);
            peerProperty.setInitTime(System.currentTimeMillis());
            RegistryInformation.setPrimaryRegistry(registry);
		    // Ailaan!
			System.out.println("Namaste!");
			System.out.println("You are the 1st one to join.");

            // Start the thread to check remaining clients 
            setActivePlayerCount(activePlayerCount++);
            activePlayerCount++;
            isPrimaryServer=true;
		}
        catch(java.lang.NullPointerException exp)
        {
            System.out.println("trying to join the server.");
            isPrimaryServer=false;
            return 1;
        }
     	catch(Exception exp)
     	{
     		System.out.println("startGameAsServer Exception"+ exp.toString());
			exp.printStackTrace();
            isPrimaryServer=false;
			return -1;
     	}
     	return 0;

     }

     public boolean startGameAsPlayer() throws RemoteException {
     	try
     	{

     	//For all the players
        String host = "//127.0.0.1:1099/server";
        //clientStub = (MazeGameP2PInterface) Naming.lookup(host);

        
        Registry registrySub = LocateRegistry.getRegistry("127.0.0.1",1099);
        clientStub = (MazeGameP2PInterface) registrySub.lookup("pserver");
        clientlifeStub = (ClientLifeChecker) registrySub.lookup("ClientLife");
        endGameStub = (GameEndChecker) registrySub.lookup("GameEnd");
        mazeGStub = (MazeGameInterface) registrySub.lookup("mazeMovement");
        RegistryInformation.setPrimaryRegistry(registrySub);

        if(!clientStub.joinGame(player_id))
            {
                System.out.println("Not successful to join the game.");
                System.exit(0);
            }

        if(mazeGStub.getPlayerActiveTime().size() == 2)
        {
            peerProperty.setBackupServer(player_id);
            peerProperty.setBackupPort(1100);
            peerProperty.isPrimaryServer(false);
            peerProperty.isBackupServer(true);
            System.out.println("Backup server is ready !");
        }
        initClientLifeThread();
        initGameEndThread();

        while(!clientStub.GetPeerProperties().hasGameStarted())
        {
            try
            {
            System.out.println("Sleeping for sometime !");
            Thread.sleep(2000);
            }
           catch (InterruptedException ie) {}
        }
        
        return true;
     	}
        catch(java.rmi.ConnectException exp)
        {
            System.out.println("Can not contact to server. Quitting.");
            exp.printStackTrace();
            return false;
        }
     	catch(Exception exp)
     	{
     		System.out.println("startGameAsPlayer"+ exp.toString());
			exp.printStackTrace();
			return false;
     	}

     }

     public void playGame() throws RemoteException
     {

     	try
        {
            //GameState gameState = new GameState();
            //Registry registry = clientStub.GetPeerProperties().getPrimaryRegistry();
            Registry registry = RegistryInformation.getPrimaryRegistry();
            //Registry registrySub = LocateRegistry.getRegistry("127.0.0.1",1099);
            mazeGStub = (MazeGameInterface) registry.lookup("mazeMovement");
            /*Registry registry = RegistryInformation.getPrimaryRegistry();
            Registry registrySub = LocateRegistry.getRegistry("127.0.0.1",1099);
            MazeGameInterface mazeGStub = null;

            mazeGStub = (MazeGameInterface) registrySub.lookup("mazeMovement");*/


            int[][] mazePlayerState_ = mazeGStub.getMazePlayerState();
            int[][] mazeTreasureState_= mazeGStub.getMazeTreasureState();
            HashMap<Integer,Integer> mazePlayerTreasures_= mazeGStub.getMazePlayerTreasures();
            int mazeSize_ = mazeGStub.getMazeSize();
            printMaze(player_id,playerName,mazeSize_,mazeTreasureState_,mazePlayerState_,mazePlayerTreasures_);
    
            while(true)
            {
                System.out.println("enter move:");
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                String move;
                move = input.readLine().toLowerCase();
            
                if(move.equals("kill") || move.equals("quit"))
                {
                    System.out.println("Player killed himself.");
                    mazePlayerState_ = mazeGStub.getMazePlayerState();
                    mazeTreasureState_= mazeGStub.getMazeTreasureState();
                    mazePlayerTreasures_= mazeGStub.getMazePlayerTreasures();
                    mazeSize_ = mazeGStub.getMazeSize();
                    printMaze(player_id,playerName,mazeSize_,mazeTreasureState_,mazePlayerState_,mazePlayerTreasures_);
                    System.exit(0);
                }
                else
                {
                    //Printing updated maze
                    if(mazeGStub.getState(move,player_id))
                    {
                        System.out.println("Updated state is:");
                    }
                    else
                    {
                        System.out.println("Invalid move !");
                    }
                    mazePlayerState_ = mazeGStub.getMazePlayerState();
                    mazeTreasureState_= mazeGStub.getMazeTreasureState();
                    mazePlayerTreasures_= mazeGStub.getMazePlayerTreasures();
                    mazeSize_ = mazeGStub.getMazeSize();
                    printMaze(player_id,playerName,mazeSize_,mazeTreasureState_,mazePlayerState_,mazePlayerTreasures_);
                }
                
            }
              
        }
        catch(java.rmi.NoSuchObjectException exp)
        {
         /*   Registry registry = RegistryInformation.getPrimaryRegistry();
            mazeGStub = (MazeGameInterface) registry.lookup("mazeMovement");*/
        }
            catch (Exception e)
            {
                System.out.println("Last state is :");
                int[][] mazePlayerState_ = mazeGStub.getMazePlayerState();
                int[][] mazeTreasureState_= mazeGStub.getMazeTreasureState();
                HashMap<Integer,Integer> mazePlayerTreasures_= mazeGStub.getMazePlayerTreasures();
                int mazeSize_ = mazeGStub.getMazeSize();
                printMaze(player_id,playerName,mazeSize_,mazeTreasureState_,mazePlayerState_,mazePlayerTreasures_);
                System.out.println("Game has ended. Dhanayvaad !");
                System.exit(0);
            }
    }

    public PeerProperties GetPeerProperties() throws RemoteException
    {
        return peerProperty;
    }

       public synchronized boolean getState(String move, int client_id) throws RemoteException
    {
        //Processing the move
        if(move.equals("kill") || move.equals("quit"))
        {
            if(mazeGame.clearPlayer(client_id))
            {
                System.out.println("Player "+client_id+" has killed himself. Removed him from the player list.");
            }
            else
            {
                System.out.println("Unable to remove Player "+client_id+" from the player list.");
            }
            return false;
        }
        else if(!mazeGame.move(move,client_id))
        {
            // return successful or unsuccessful for move
            return false;       
        }
        // check if all the treasures have been allocated
        return true;
    }

    public synchronized void printMaze(int player_id,String player_name,int mazeSize,int[][] mazeTreasureState,int[][] mazePlayerState,HashMap<Integer,Integer> mazePlayerTreasures) throws RemoteException
      {
        try
        {
        for (int i = 0; i < mazeSize; i++)
        {

            for (int j = 0; j < mazeSize; j++)
             { 
                if(j == mazeSize-1)
                {
                   System.out.printf("+----------------+");
                }
                else
                {
                   System.out.printf("+----------------"); 
                }
            }

            System.out.printf("%n");

            for (int j = 0; j < mazeSize; j++) 
            { 
               int content = mazeTreasureState[i][j];
               int playerLocation = mazePlayerState[i][j];

               if(j == mazeSize-1)
               {
                  //if a player is present at a position, print its details else print the treasure present at that location
                  if(playerLocation ==0)
                  {
                      System.out.format("|       %-3d      |",content);
                  }
                  else
                  {
                     if((0!=player_id) &&(playerLocation==player_id))
                     {
                        System.out.print("|     "+player_name+"    ");
                     }
                     else
                     {
                        System.out.format("| %-3d",playerLocation);
                      }
                     for(Map.Entry<Integer,Integer> treasure : mazePlayerTreasures.entrySet())
                     {
                        if(treasure.getKey() == playerLocation)
                        {
                          System.out.print("(");
                          System.out.format("%-3d",treasure.getValue());
                          System.out.print(") |");
                        }
                      }
                  }
                  
                continue;
              } 
              else
              {
                //if a player is present at a position, print its details else print the treasure present at that location
                  if(playerLocation ==0)
                  {
                      System.out.format("|       %-3d      ",content);
                  }
                  else
                  {
                     if((0!=player_id) && (playerLocation==player_id))
                     {
                        System.out.print("|     "+player_name+"    ");
                     }
                     else
                     {
                        System.out.format("| %-3d",playerLocation);
                      }
                     for(Map.Entry<Integer,Integer> treasure : mazePlayerTreasures.entrySet())
                     {
                        if(treasure.getKey() == playerLocation)
                        {
                          System.out.print("(");
                          System.out.format("%-3d",treasure.getValue());
                          System.out.print(") ");
                        }
                      }
                  }
              }
            }

            if(i == mazeSize-1 )
            {
              System.out.printf("%n");
              for (int j = 0; j < mazeSize; j++) 
              { 
                if(j == mazeSize-1)
                {
                  System.out.printf("+----------------+");
                }
                else 
                {
                  System.out.printf("+----------------"); 
                } 
              }
            }

            System.out.printf("%n");
        }
       }
       catch (Exception exp)
       {
           System.out.println("Exception");
           exp.printStackTrace();
            System.exit(0);
       }
    }

}