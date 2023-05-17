package game;


import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static game.Main.simulation;

public class GhostFigure extends Thread{


    Random RAND=new Random();
    public static int MINIMUM_NUMBER_OF_DIAMONDS=2;


    public GhostFigure(){
        createGhostPath();

    }

    public void createGhostPath(){

        for(int previousIndexOfDiamond: simulation.ghostPath)
        {

            previousIndexOfDiamond--;
            int diamondX=previousIndexOfDiamond / simulation.getDimensionOfMatrix();
            int diamondY=previousIndexOfDiamond % simulation.getDimensionOfMatrix();
            simulation.matrix[diamondX][diamondY]=null;

        }
        simulation.ghostPath.clear();
        simulation.diamondsList.clear();
        int numberOfDiamonds=RAND.nextInt(simulation.getDimensionOfMatrix()-MINIMUM_NUMBER_OF_DIAMONDS)+MINIMUM_NUMBER_OF_DIAMONDS;
        int lengthOfPath=simulation.diamondCirclePath.size();
        for(int i=0;i<numberOfDiamonds;i++){

            int elementOnPath=simulation.diamondCirclePath.get(RAND.nextInt(lengthOfPath));
            int x=(elementOnPath-1) / simulation.getDimensionOfMatrix();
            int y=(elementOnPath-1) % simulation.getDimensionOfMatrix();
            if(!simulation.ghostPath.contains(elementOnPath) && !(simulation.matrix[x][y] instanceof Figure)){
                simulation.ghostPath.add(elementOnPath);
            }
        }
    }

    @Override
    public void run() {

        int dimensionOfMatrix= simulation.getDimensionOfMatrix();
        while(simulation.isIsRunning()) {

            if( simulation.runningTime>4000) {
                for (int element : simulation.ghostPath) {
                    if (!simulation.isIsRunning()) break;


                    element--;
                    int x = element / dimensionOfMatrix;
                    int y = element % dimensionOfMatrix;
                    if (simulation.matrix[x][y] == null || simulation.matrix[x][y] instanceof Diamond) {
                        Diamond diamond = new Diamond();
                        simulation.matrix[x][y] = diamond;

                    }


                }
            }
                try {
                    Thread.sleep(Simulation.GHOST_SLEEP_TIME);
                } catch (InterruptedException e) {
                    Logger.getLogger(Simulation.class.getName()).log(Level.WARNING,e.fillInStackTrace().toString());
                }

                createGhostPath();

        }
    }
}
