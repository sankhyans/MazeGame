import java.util.*;

public class Grids {

      public int mazeSize;
      public int x = 0;
      public int y = 0;

      public synchronized void printMaze(GameState gameState, int player_id,String player_name)
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
               int content = gameState.mazeTreasureState[i][j];
               int playerLocation = gameState.mazePlayerState[i][j];

               if(j == mazeSize-1)
               {
                  //if a player is present at a position, print its details else print the treasure present at that location
                  if(playerLocation ==0)
                  {
                      System.out.format("|       %-3d      |",content);
                  }
                  else
                  {
                     if(playerLocation==player_id)
                     {
                        System.out.print("|     "+player_name+"    ");
                     }
                     else
                     {
                        System.out.format("| %-3d",playerLocation);
                      }
                     for(Map.Entry<Integer,Integer> treasure : gameState.mazePlayerTreasures.entrySet())
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
                     if(playerLocation==player_id)
                     {
                        System.out.print("|     "+player_name+"    ");
                     }
                     else
                     {
                        System.out.format("| %-3d",playerLocation);
                      }
                     for(Map.Entry<Integer,Integer> treasure : gameState.mazePlayerTreasures.entrySet())
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
       catch(java.lang.NullPointerException exp){}
    }
}