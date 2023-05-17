package game;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static game.Main.simulation;

public abstract class Figure extends  Thread {

    private int id;
    private Color color;
    private String name;
    private int numberOfJumps;
    private boolean started=false;
    private boolean done=false;
    private boolean dead=false;

    private int roadLengthOfFigure=0;
    public int figureStartIndex=0;
    public int figureEndIndex=0;
    private int numberOfDiamonds = 0;

    public ArrayList<Integer> figurePath=new ArrayList<>();

    public long timeOfMovingFigure;

    public final Object LOCK = new Object();



    public Figure(){
        super();
        this.id=simulation.figureCounter;
        this.name="figure_"+(++simulation.figureCounter);

    }


    public Figure(Color color) {
        this.color=color;
        this.id=simulation.figureCounter;
        this.name="figure_"+(++simulation.figureCounter);
    }

    public  Figure(Color color,int numberOfJumps){
        this.color=color;
        this.id=simulation.figureCounter;
        this.numberOfJumps=numberOfJumps;
        this.name="figure_"+(++simulation.figureCounter);

    }

    @Override
    public void run() {
        this.started=true;

        synchronized (LOCK){

            while(this.roadLengthOfFigure!=simulation.diamondCirclePath.size())
            {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    Logger.getLogger(Simulation.class.getName()).log(Level.WARNING,e.fillInStackTrace().toString());
                }
                long startOfMoving=System.currentTimeMillis();
                if(this.dead)
                {
                    this.done=true;
                }else
                {
                    int previousIndex;
                    int dimensionOfMatrix=simulation.getDimensionOfMatrix();
                    if (roadLengthOfFigure > 0 && numberOfJumps + numberOfDiamonds > 0) {
                        previousIndex = simulation.diamondCirclePath.get(roadLengthOfFigure - 1) - 1;
                        int previousX = previousIndex / dimensionOfMatrix ;
                        int previousY = previousIndex % dimensionOfMatrix;

                        simulation.matrix[previousX][previousY] = null;
                    }


                    int indexOfFigure;
                    int x;
                    int y;
                    int tempValueOfJumps=numberOfJumps;
                    for(int i=0;roadLengthOfFigure<simulation.diamondCirclePath.size() && i<tempValueOfJumps+numberOfDiamonds;i++,roadLengthOfFigure++) {
                        figureStartIndex=simulation.diamondCirclePath.get(roadLengthOfFigure);
                        synchronized (simulation.lock) {
                            try {
                                if (simulation.isPause())

                                    simulation.lock.wait();
                            } catch (InterruptedException e) {
                              Logger.getLogger(Simulation.class.getName()).log(Level.WARNING,e.fillInStackTrace().toString());
                            }
                        }

                        if(roadLengthOfFigure==0)
                        {
                            tempValueOfJumps++;
                        }
                        System.out.println(Thread.currentThread().getName());



                        indexOfFigure = simulation.diamondCirclePath.get(roadLengthOfFigure)-1;
                        x = indexOfFigure / dimensionOfMatrix;
                        y = indexOfFigure % dimensionOfMatrix;

                        if (roadLengthOfFigure < simulation.diamondCirclePath.size()-1)
                        {
                            figureEndIndex = simulation.diamondCirclePath.get(roadLengthOfFigure + 1);
                        }
                        Object tempObject = simulation.matrix[x][y];

                        if(tempObject instanceof Diamond)
                        {
                            System.out.println("Pokupljen dijamant");
                            numberOfDiamonds++;
                            for(int j=0;j<simulation.ghostPath.size();j++)
                            {
                                    if(simulation.ghostPath.get(j)==(x*simulation.getDimensionOfMatrix())+y+1)
                                    {
                                        simulation.ghostPath.remove(j);
                                    }
                            }
                            simulation.matrix[x][y] = null;
                        }

                        if(tempObject instanceof Figure && (i+1==tempValueOfJumps+numberOfDiamonds))

                        {
                            tempValueOfJumps++;
                        }

                        if(!(tempObject instanceof Figure))
                        {
                            simulation.matrix[x][y]=this;

                        }
                        try {
                            Thread.sleep(Simulation.SECOND_SLEEP_TIME);
                        } catch (InterruptedException e) {
                          Logger.getLogger(Simulation.class.getName()).log(Level.WARNING,e.fillInStackTrace().toString());
                        }
                        if ((i + 1 < tempValueOfJumps + numberOfDiamonds || simulation.diamondCirclePath.get(roadLengthOfFigure)==simulation.diamondCirclePath.get(simulation.diamondCirclePath.size()-1)) && !(tempObject instanceof Figure)) {

                            simulation.matrix[x][y]=null;
                        }
                        figurePath.add(indexOfFigure+1);

                    }
                    numberOfDiamonds=0;

                }
                long endOfMoving=System.currentTimeMillis();
                timeOfMovingFigure+=(endOfMoving-startOfMoving);
                LOCK.notify();


            }

        }
        done=true;

    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getNumberOfJumps(){
        return numberOfJumps;
    }

    public void setNumberOfJumps(int numberOfJumps) {
        this.numberOfJumps=numberOfJumps;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isDone(){
        return done;
    }


    public int getFigureId() {
        return id;
    }


    public String getFigureName() {
        return name;
    }

    public int getRoadLengthOfFigure(){
        return this.roadLengthOfFigure;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

}
