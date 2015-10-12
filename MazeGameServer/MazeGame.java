import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MazeGame extends UnicastRemoteObject {

    public int mazeSize;
    public int treasureCount;
    public int maxPlayerCount;
	public int[][] mazePlayerState;
	public int[][] mazeTreasureState;
	public HashMap<Integer,Integer> mazePlayerTreasures = new HashMap<Integer,Integer>(); //<client_id,client_treasures>
	public HashMap<ClientConnect,Integer> playerList = new HashMap<ClientConnect,Integer>(); //<Client object,client_id>
    public HashMap<Integer,Long> playerActiveTime = new HashMap<Integer,Long>(); //<client_id,client_treasures>

	// Constructor:
	// To generate the maze game.
    public MazeGame(int mazeSize_, int treasureCount_) throws RemoteException {
        mazeSize = mazeSize_;
        treasureCount = treasureCount_;

		if (mazeSize%2 == 0) {
			maxPlayerCount = (mazeSize*mazeSize)/2;
		}
		else {
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

        while(!allocated) {
    		
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

        while(treasuresUnallocated != 0) {
        	scoopOfTreasure = randomNum.nextInt(treasuresUnallocated + 1); // + 1, As the upper bound is excluded
    		x = randomNum.nextInt(mazeSize);
    		y = randomNum.nextInt(mazeSize);

        	if(mazeTreasureState[x][y] == 0) {
        		mazeTreasureState[x][y] = scoopOfTreasure;
        		treasuresUnallocated = treasuresUnallocated - scoopOfTreasure;        		
        	}
        }
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
        for(Map.Entry<ClientConnect,Integer> player : playerList.entrySet()) 
        {
            if(player.getValue() == client_id) 
            {
                ClientConnect client = player.getKey();
                if(tresureAtLocation ==0)
                {
                    client.message("Treasure not found ! Current treasure count is "+ currentTreasure +".","INFO");
                }
                else if(tresureAtLocation>0)
                {
                    client.message("Treasure found ! Updated treasure count is " + currentTreasure +".","INFO");
                }
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

    public int[] searchForClient(int client_id){
        int[] position = new int[2];
        int numRows = mazePlayerState.length;
        int x = 0;
        int y = 0;
        for(x=0;x<numRows;x++){
            int[] columnArray = mazePlayerState[x];
            int numColumns = columnArray.length;
            for(y=0;y<numColumns;y++){
                if(mazePlayerState[x][y] == client_id){
                    position[0] = x;
                    position[1] = y;
                }
            }
        }
        return position;
    }
    
    public int remainingTreasures()
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