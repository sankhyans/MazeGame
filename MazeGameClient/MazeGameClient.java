import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MazeGameClient extends UnicastRemoteObject implements ClientConnect{

	public static int player_id;
    public static String player_name;
	public static int mazeSize;
    public static boolean gameJoined;
    public static GameState gameState = null;
    public static MazeGameMethods clientStub = null;
    public static ClientLifeChecker clientlifeStub = null;
    public static boolean gameRunning = false;
    public static Grids grid = new Grids();
	public HashMap<ClientConnect,Integer> playerList = new HashMap<ClientConnect,Integer>();
	
    public MazeGameClient() throws RemoteException {
    	super();
    	Random gen = new Random();
    	player_id = gen.nextInt(Integer.MAX_VALUE) + 1;
        gameState = new GameState();

        try {

            String host = "//127.0.0.1:1099/server";
            clientStub = (MazeGameMethods) Naming.lookup(host);

            Registry registrySub = LocateRegistry.getRegistry("127.0.0.1",1099);
            clientlifeStub = (ClientLifeChecker) registrySub.lookup("ClientLife");

        }
        catch (Exception exp) {
            System.err.println("Client exception: " + exp.toString());
            exp.printStackTrace();
            System.exit(1);
        }

    }

	public MazeGameClient(int player_id_) throws RemoteException {
    	super();    	
    	player_id = player_id_;
    }    

    // This function processes the callback 
    // form the server to the client.
    // The possible message types are defined in
    // the MessageTypes enum
	public void message(String msg, String type) throws RemoteException {

        MessageTypes msgType = null;
        try{
            msgType = MessageTypes.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException exp){
            
        }

    	switch(msgType){
    		case INFO: // get messages to print
    			System.out.println(msg);
    			break;
    		case MAZE_SIZE: // Get maze dimensions
    			mazeSize = Integer.parseInt(msg);
                grid.mazeSize = mazeSize;
    			break;
            case SOG: // Start of game
                gameState = clientStub.getState();
                grid.printMaze(gameState,player_id,player_name);
                playGame();
                break;
    		case EOG: // End of game
                grid.printMaze(gameState,player_id,player_name);
                System.out.println("\n\n"+msg);
    			System.out.println("The game has ended! Alvida :)");
                System.exit(0);
    			break;
            default:
                break;
    	}
    }

    public static void playGame() throws RemoteException {
        try{
        while(gameRunning){
            System.out.print("Enter your move: ");
            Scanner scanner = new Scanner(System.in);
            String move = scanner.nextLine();

            //Printing updated maze
            gameState = clientStub.getState(move,player_id);
            grid.printMaze(gameState,player_id,player_name);
        }
    }
    catch (java.rmi.ServerException exp)
        {
            System.out.println("Server has died.");
            System.exit(0);
        }
        catch (java.rmi.UnmarshalException exp)
        {
            System.out.println("Dhanayvaad.");
            System.exit(0);
        }


    }

    
    public static void main(String args[]) throws RemoteException {    	

        MazeGameClient mazeGameClient = new MazeGameClient();
        player_name = args[0];

        System.out.println("Namaste "+player_name+"!");
        System.out.println("Joining Maze Game...");
        LifeLine lifeLine = new LifeLine();
        Thread clientLifeThread = new Thread(lifeLine);
        
        try{
            gameJoined = clientStub.joinGame(mazeGameClient, mazeGameClient.player_id);

            if(!gameJoined)
            {
                System.out.println("Exiting game!");
                System.exit(0);
            }
            else
            {
                lifeLine.setPlayerID(mazeGameClient.player_id);
                lifeLine.setStub(clientlifeStub);
                clientLifeThread.start();
              try
              {
                while(true)
                {
                    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                    String move;
                    move = input.readLine().toLowerCase();
            
                    //Printing updated maze
                    gameState = clientStub.getState(move,player_id);
                    grid.printMaze(gameState,player_id,player_name);
                    
                }
              }
              catch (java.rmi.ServerException exp)
            {  
                
                System.out.println("Last state is :");
                grid.printMaze(gameState,player_id,player_name);
                System.out.println("Game has ended. Dhanayvaad !");
                System.exit(0);
            }
            catch (java.rmi.ConnectException exp)
            {
                System.out.println("Last state is :");
                grid.printMaze(gameState,player_id,player_name);
                System.out.println("Game has ended. Dhanayvaad !");
                System.exit(0);
            }
            catch(java.rmi.UnmarshalException exp)
            {
                System.out.println("Last state is :");
                grid.printMaze(gameState,player_id,player_name);
                System.out.println("Game has ended. Dhanayvaad !");
                System.exit(0);   
            }
            catch(java.io.EOFException exp)
            {
                System.out.println("Last state is :");
                grid.printMaze(gameState,player_id,player_name);
                System.out.println("Game has ended. Dhanayvaad !");
                System.exit(0);
            }
            catch (Exception e)
            {
                System.err.println("Client exception: " + e.toString());
                e.printStackTrace();
                System.exit(0);
              }
            }
          }
        catch (java.rmi.ConnectException exp)
        {
            System.out.println("Connection refused! Kindly check if the server is running.");
            System.exit(0);
        }
        catch (java.rmi.ServerException exp)
        {
            System.out.println("Server has died.");
            System.exit(0);
        }

    }
}

