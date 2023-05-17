package game;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static game.Main.simulation;

public class Player extends Thread {

    private String name;
    private static final int NUMBER_OF_FIGURES=4;
    private ArrayList<Figure> figureList = new ArrayList<>(NUMBER_OF_FIGURES);
    Random RAND = new Random();

    private boolean started=false;
    private boolean done=false;

    public int numberOfDeadFigures=0;
    public Figure currentFigure;

    public final Object LOCK = new Object();

    public Player(String name, Color color) {
        this.name=name;
        createFiguresForPlayer(color);
       // System.out.println("fffff");
    }



    public ArrayList<Figure> getFigureList() {
        return figureList;
    }

    void createFiguresForPlayer(Color color) {

        for(int i=0;i<NUMBER_OF_FIGURES;i++)
        {
            int randomValue = RAND.nextInt(3);
            Figure figure=null;
            if (randomValue==0)
            {
                figure=new BasicFigure(color);
            }
            if (randomValue==1)
            {
                figure=new FastFigure(color);
            }
            if (randomValue==2)
            {
                figure=new FlyingFigure(color);
            }
            figureList.add(figure);

        }


    }

    @Override
    public void run() {

        this.started=true;
        synchronized (this.LOCK) {
            while (numberOfDeadFigures < NUMBER_OF_FIGURES) {

                try {
                    this.LOCK.wait();
                } catch (InterruptedException e) {
                    Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
                }
                currentFigure=figureList.get(numberOfDeadFigures);
                currentFigure.setNumberOfJumps(simulation.getNumberOfSteps());
                if(!currentFigure.isStarted())
                {
                    currentFigure.start();
                    try {
                        Thread.sleep(Simulation.SECOND_SLEEP_TIME);
                    } catch (InterruptedException e) {
                        Logger.getLogger(Simulation.class.getName()).log(Level.WARNING,e.fillInStackTrace().toString());
                    }
                }
                synchronized (currentFigure.LOCK){

                    currentFigure.LOCK.notify();
                    try {

                        if(currentFigure.isDone())
                            numberOfDeadFigures++;
                        else
                            currentFigure.LOCK.wait();
                    } catch (InterruptedException e) {
                       Logger.getLogger(Simulation.class.getName()).log(Level.WARNING,e.fillInStackTrace().toString());
                    }

                }
                this.LOCK.notify();
            }
        }
        this.done=true;
    }


    public String getPlayerName() {
        return name;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isDone(){
        return done;
    }

}
