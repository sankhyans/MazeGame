import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.io.Serializable;

public class MazeGame extends UnicastRemoteObject implements MazeGameInterface,Serializable {

    public static int mazeSize;
    public boolean joinFlag=false;
    public int joinTime=5;
    public static int treasureCount;
    public int maxPlayerCount;
	public static int[][] mazePlayerState;
	public static int[][] mazeTreasureState;
	static public HashMap<Integer,Integer> mazePlayerTreasures = new HashMap<Integer,Integer>(); //<client_id,client_treasures>
    public static HashMap<Integer,Long> playerActiveTime = new HashMap<Integer,Long>(); //<client_id,client_treasures>
    

	// Constructor:
	// To generate the maze game.
    public MazeGame() throws RemoteException {
        super();
        joinFlag = true;
    }

    public void setMazeSize(int mazeSize_) throws RemoteException
    {
        mazeSize=mazeSize_;
    }
    public void setTreasureCount(int count) throws RemoteException
    {
        treasureCount=count;
    }
    public int getMazeSize() throws RemoteException
    {
        return mazeSize;
    }
    public int getTreasureCount() throws RemoteException
    {
        return treasureCount;
    }
    public int[][] getMazePlayerState() throws RemoteException
    {
        return mazePlayerState;
    }
    public int[][] getMazeTreasureState() throws RemoteException
    {
        return mazeTreasureState;
    }
    public HashMap<Integer,Integer> getMazePlayerTreasures() throws RemoteException
    {
        return mazePlayerTreasures;
    }
    public HashMap<Integer,Long> getPlayerActiveTime() throws RemoteException
    {
        return playerActiveTime;
    }

    public void init() throws RemoteException
    {
        if (mazeSize%2 == 0)
        {
            maxPlayerCount = (mazeSize*mazeSize)/2;
        }
        else
        {
            maxPlayerCount = ((mazeSize*mazeSize)-1)/2;
        }

        mazePlayerState = new int[mazeSize][mazeSize];
        mazeTreasureState = new int[mazeSize][mazeSize];

        // The maze parameters are set
        // we can proceed to distributing the treasures
        // randomly across the maze.
        allocateTreasureLocation();
    }
	
	// Allocating player locations to the maze
	// Generating Random Player Locations
    public void allocatePlayerLocation(int player_id) throws RemoteException {
        
		int x,y;
        Random randomNum = new Random();
        boolean allocated = false;

        while(!allocated)
        {
    		
    		x = randomNum.nextInt(mazeSize);
    		y = randomNum.nextInt(mazeSize);

        	if(mazePlayerState[x][y] == 0)
            {
        		mazePlayerState[x][y] = player_id;
                mazePlayerTreasures.put(player_id,mazeTreasureState[x][y]);
                mazeTreasureState[x][y]=0;
        		allocated = true;
        		System.out.println(player_id+" allocated ("+x+","+y+")");
        	}
        }
    
    }

	// Allocating treasure locations to the maze
	// Generating Random treasure Locations
    public void allocateTreasureLocation() throws RemoteException {
        
        int treasuresUnallocated = treasureCount;
        Random randomNum = new Random();
		int x,y,scoopOfTreasure;
        boolean allocated = false;

        while(treasuresUnallocated != 0)
        {
        	scoopOfTreasure = randomNum.nextInt(treasuresUnallocated + 1); // + 1, As the upper bound is excluded
    		x = randomNum.nextInt(mazeSize);
    		y = randomNum.nextInt(mazeSize);

        	if(mazeTreasureState[x][y] == 0)
            {
        		mazeTreasureState[x][y] = scoopOfTreasure;
        		treasuresUnallocated = treasuresUnallocated - scoopOfTreasure;        		
        	}
        }
    }


// This function is invoked by the client for the following information:
    // Location of all the players in maze
    // Location of all the treasures in maze
    // count of newly collected treasures (if any)
    // to all the clients.
 /*   public synchronized MazeGame getState() throws RemoteException {
        
        //updateGameState();
        return mazeGame;
    }
*/
    public synchronized boolean getState(String move, int client_id) throws RemoteException
    {
        //updateMazeGame();
        
        //Processing the move
        if(move.equals("kill") || move.equals("quit"))
        {
            if(clearPlayer(client_id))
            {
                System.out.println("Player "+client_id+" has killed himself. Removed him from the player list.");
            }
            else
            {
                System.out.println("Unable to remove Player "+client_id+" from the player list.");
            }
            return false;
        }
        else if(!move(move,client_id))
        {
            // return successful or unsuccessful for move
            return false;       
        }
        // check if all the treasures have been allocated
        return true;
    }

    // move function to process the move commands
    // issued by the client
    public synchronized boolean move(String moveName, int client_id) throws RemoteException
    {
        
        Directions move = null;
        
        try{
            move = Directions.valueOf(moveName.toLowerCase());
        }
        catch (IllegalArgumentException exp)
        {
            return false;
        }

        int[] currentPosition = new int[2];
        switch(move){
            // moving north
            case n:
            case north:
            case uttar:
                currentPosition = searchForClient(client_id);
                if(currentPosition[0] == 0)
                { 
                    return false;
                }
                else if(0 != mazePlayerState[currentPosition[0]-1][currentPosition[1]])
                {
                    return false;
                }
                else
                {
                    int h = currentPosition[0]-1;
                    System.out.println("new position for "+client_id+":"+h+","+currentPosition[1]);
                    mazePlayerState[currentPosition[0]-1][currentPosition[1]] = client_id;
                    mazePlayerState[currentPosition[0]][currentPosition[1]] = 0;

                    //Updating Player treasures
                    int tresureAtLocation=mazeTreasureState[currentPosition[0]-1][currentPosition[1]];
                    mazeTreasureState[currentPosition[0]-1][currentPosition[1]]=0;
                    updateTreasure(client_id, tresureAtLocation);
                    
                }
                break;

            case s:
            case south:
            case dakshin:
                currentPosition = searchForClient(client_id);
                if(currentPosition[0] == mazeSize-1)
                { 
                    return false;
                }
                else if(0 != mazePlayerState[currentPosition[0]+1][currentPosition[1]])
                {
                    return false;
                }
                else
                {
                    int h = currentPosition[0]+1;
                    System.out.println("new position for "+client_id+":"+h+","+currentPosition[1]);
                    mazePlayerState[currentPosition[0]+1][currentPosition[1]] = client_id;
                    mazePlayerState[currentPosition[0]][currentPosition[1]] = 0;

                    //Updating Player treasures
                    int tresureAtLocation=mazeTreasureState[currentPosition[0]+1][currentPosition[1]];
                    mazeTreasureState[currentPosition[0]+1][currentPosition[1]]=0;
                    updateTreasure(client_id, tresureAtLocation);
                }
                break;

            case e:
            case east:
            case purav:
                currentPosition = searchForClient(client_id);
                if(currentPosition[1]==0){ 
                    return false;
                }
                else if(0 != mazePlayerState[currentPosition[0]][currentPosition[1]-1])
                {
                    return false;
                }
                else {
                    int h = currentPosition[1]-1;
                    System.out.println("new position for "+client_id+":"+currentPosition[0]+","+h);
                    mazePlayerState[currentPosition[0]][currentPosition[1]-1] = client_id;
                    mazePlayerState[currentPosition[0]][currentPosition[1]] = 0;

                    //Updating Player treasures
                    int tresureAtLocation=mazeTreasureState[currentPosition[0]][currentPosition[1]-1];
                    mazeTreasureState[currentPosition[0]][currentPosition[1]-1]=0;
                    updateTreasure(client_id, tresureAtLocation);
                }
                break;

            case w:
            case west:
            case paschim:
                currentPosition = searchForClient(client_id);
                if(currentPosition[1]==mazeSize-1){ 
                    return false;
                }
                else if(0 != mazePlayerState[currentPosition[0]][currentPosition[1]+1])
                {
                    return false;
                }
                else {
                    int h = currentPosition[1]+1;
                    System.out.println("new position for "+client_id+":"+currentPosition[0]+","+h);
                    mazePlayerState[currentPosition[0]][currentPosition[1]+1] = client_id;
                    mazePlayerState[currentPosition[0]][currentPosition[1]] = 0;

                    //Updating Player treasures
                    int tresureAtLocation=mazeTreasureState[currentPosition[0]][currentPosition[1]+1];
                    mazeTreasureState[currentPosition[0]][currentPosition[1]+1]=0;
                    updateTreasure(client_id, tresureAtLocation);
                }
                break;
            case nm:
                updateTreasure(client_id,0);
                break;
            default:
            	// for invalid keystrokes
                return false;

        }

        return true;
    }

    public void updateTreasure(int client_id, int tresureAtLocation) throws RemoteException
    {
        int currentTreasure=0;
        for(Map.Entry<Integer,Integer> treasure : mazePlayerTreasures.entrySet())
        {
            if(treasure.getKey() == client_id)
            {
                currentTreasure= treasure.getValue();
                currentTreasure += tresureAtLocation;
                mazePlayerTreasures.put(client_id,currentTreasure);
            }
        }
    }

    public synchronized void updateLastActiveTime(int client_id)
    {
        for(Map.Entry<Integer,Long> player : playerActiveTime.entrySet())
        {
            if(player.getKey() == client_id)
            {
               playerActiveTime.put(client_id,System.currentTimeMillis());
            }
        }
    }

    public int[] searchForClient(int client_id)
    {
        int[] position = new int[2];
        int numRows = mazeSize;
        int x = 0;
        int y = 0;
        for(x=0;x<numRows;x++)
        {
            int[] columnArray = mazePlayerState[x];
            int numColumns = mazeSize;
            for(y=0;y<numColumns;y++)
            {
                if(mazePlayerState[x][y] == client_id)
                {
                    position[0] = x;
                    position[1] = y;
                }
            }
        }
        return position;
    }
    
    
    public synchronized boolean clearPlayer(int client_id)
    {
        int numRows = mazePlayerState.length;
        int x = 0;
        int y = 0;
        for(x=0;x<numRows;x++){
            int[] columnArray = mazePlayerState[x];
            int numColumns = columnArray.length;
            for(y=0;y<numColumns;y++){
                if(mazePlayerState[x][y] == client_id)
                {
                    mazePlayerState[x][y]=0;
                }
            }
        }
        return true;
    }

    public int getRemainingTreasures()
    {
        int treasure=0;
        for (int x =0;x<mazeSize;x++)
        {
            for ( int y=0;y<mazeSize;y++)
            {
                treasure+=mazeTreasureState[x][y];
            }
        }
        return treasure;
    }
}