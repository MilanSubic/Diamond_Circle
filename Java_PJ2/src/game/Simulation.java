package game;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;


import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

import static game.Main.simulation;

public class Simulation {

    private int dimensionOfMatrix;
    private int numberOfPlayers;
    private int numberOfSteps;
    public Object[][] matrix;

    public LinkedList<Player> playersList=new LinkedList<>();
    public ArrayList<Integer> ghostPath=new ArrayList<>();
    public ArrayList<Integer> holes=new ArrayList<>();

    public Button[] figureButtonsList;

    public StringBuilder resultStringBuilder=new StringBuilder();

    public int indexOfPressedButton;

    public int figureCounter=0;

    public long runningTime=0;

    public final Object lock=new Object();

    public Card currentCard;
    public Player currentPlayer;

    public static int NUMBER_OF_EACH_BASIC_CARDS=10; // 4 * 10 = 40
    public static int NUMBER_OF_SPECIAL_CARDS=12;
    public static int MINIMUM_NUMBER_OF_HOLES=2;
    public static int MAXIMUM_NUMBER_OF_HOLES=5;

    public static long GHOST_SLEEP_TIME=5000;
    public static long SECOND_SLEEP_TIME=1000;

    public static String PATH_FILES="src/path/pathFile.txt";
    public static String PATH_OF_RESULT_DIRECTORY="src/result";

    public LinkedList<Card> allCardsList=new LinkedList<>(); //lista svih karata

    public ArrayList<Integer> diamondCirclePath=new ArrayList<>();

    public Random RAND=new Random();
    private boolean isRunning=false;
    private boolean pause=false;
    public int counter=0; // counter for pressing start button

    public int numberOfDonePlayers=0;

    public HashMap<Diamond,Integer> addDaimond=new HashMap<>();
    public ArrayList<Diamond> diamondsList=new ArrayList<>();

    public Simulation(){
        isRunning=true;
    }

    public int getDimensionOfMatrix() {
        return dimensionOfMatrix;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setDimensionOfMatrix(int dimensionOfMatrix) {
        this.dimensionOfMatrix = dimensionOfMatrix;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }


    public static Handler handler;

    {
        try {
            handler = new FileHandler("simulation.log");
            Logger.getLogger(Simulation.class.getName()).addHandler(handler);
        } catch (IOException e) {
            Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
        }
    }


    public void createPlayers(){

        ArrayList<Color> colors=new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);

        Collections.shuffle(colors);
        for(int i=0;i<numberOfPlayers;i++)
        {
           Player tempPlayer=new Player("player"+ (i+1),colors.get(i));
           playersList.add(tempPlayer);

        }
    }

    public void createCards(){

        for(int i=0;i<NUMBER_OF_EACH_BASIC_CARDS;i++){

            allCardsList.add(new BasicCard(1)); // 10 od 1
            allCardsList.add(new BasicCard(2)); // 10 od 2
            allCardsList.add(new BasicCard(3)); // 10 od 3
            allCardsList.add(new BasicCard(4)); // 10 od 4
        }

        int high=MAXIMUM_NUMBER_OF_HOLES;
        int low=MINIMUM_NUMBER_OF_HOLES;
        for(int i=0;i<NUMBER_OF_SPECIAL_CARDS;i++){
            int tempValue=RAND.nextInt(high-low+1)+low;
            allCardsList.add(new SpecialCard(tempValue));

        }

        Collections.shuffle(allCardsList);

    }

    public void choosePathFromDimension() throws IOException {
        BufferedReader reader;
        reader=new BufferedReader(new FileReader(PATH_FILES));
        String line=reader.readLine();
        while(line!=null){
            String tempValue;
            if(line.substring(0,1).equals(String.valueOf(dimensionOfMatrix))) {
                tempValue = line.substring(2);
                String[] tempArray = tempValue.split(",");
                for (String element : tempArray) {
                    diamondCirclePath.add(Integer.parseInt(element));
                }
            }
            if(line.substring(0,2).equals(String.valueOf(dimensionOfMatrix))){
                tempValue = line.substring(3);
                String[] tempArray = tempValue.split(",");
                for (String element : tempArray) {
                    diamondCirclePath.add(Integer.parseInt(element));
                }
            }
            line=reader.readLine();

        }reader.close();


    }

    public void startGame() throws InterruptedException, IOException {
        GhostFigure ghostFigure=new GhostFigure();
        ghostFigure.start();

        LinkedList<Player> tempList=new LinkedList<>(playersList);
        while(simulation.isIsRunning()){

            currentCard=allCardsList.getFirst();
            allCardsList.removeFirst();
            allCardsList.addLast(currentCard);

            if(currentCard instanceof BasicCard)
            {
                numberOfSteps=((BasicCard) currentCard).getCardValue();
                currentPlayer=tempList.getFirst();

                if(!currentPlayer.isStarted())
                {
                    currentPlayer.start();
                    System.out.println(currentPlayer.getPlayerName());
                    Thread.sleep(SECOND_SLEEP_TIME);
                }
                synchronized (currentPlayer.LOCK){

                    currentPlayer.LOCK.notify();
                    try {
                        if (!currentPlayer.isDone()) {

                            currentPlayer.LOCK.wait();
                            tempList.addLast(currentPlayer);
                        }

                        else {

                            numberOfDonePlayers++;

                            if (numberOfDonePlayers == numberOfPlayers) {
                                isRunning = false;
                                saveResultOfGame();

                            }
                        }
                        tempList.removeFirst();
                    }catch (InterruptedException e) {
                        Logger.getLogger(Simulation.class.getName()).log(Level.WARNING,e.fillInStackTrace().toString());
                    }

                }
            }
            else {
                if(currentCard instanceof SpecialCard)
                {

                        int numberOfHoles=((SpecialCard) currentCard).getHolesValue();
                        int lengthOfPath=simulation.diamondCirclePath.size();
                        for(int i=0;i<numberOfHoles;i++)
                        {
                            int holeOnPath=simulation.diamondCirclePath.get(RAND.nextInt(lengthOfPath));
                            if(!simulation.holes.contains(holeOnPath)){
                                simulation.holes.add(holeOnPath);
                            }
                            holeOnPath--;
                            int x=holeOnPath / dimensionOfMatrix;
                            int y=holeOnPath % dimensionOfMatrix;

                            if(simulation.matrix[x][y] instanceof FastFigure || simulation.matrix[x][y] instanceof BasicFigure)
                            {
                                System.out.println(((Figure) simulation.matrix[x][y]).getFigureName()+"je propala na poziciji"+x+"  "+y);
                                ((Figure) simulation.matrix[x][y]).setDead(true);
                                simulation.matrix[x][y]=null;
                            }
                        }
                        try {
                            Thread.sleep(SECOND_SLEEP_TIME*2); // dvije sekunde zbog prikaza
                        }catch (InterruptedException e){
                           Logger.getLogger(Simulation.class.getName()).log(Level.WARNING,e.fillInStackTrace().toString());
                        }
                        holes.clear();

                }

            }

        }

    }

    private void saveResultOfGame() throws IOException {

    try {

        long currentTimeInMillis=System.currentTimeMillis();
        FileWriter fw = new FileWriter(simulation.PATH_OF_RESULT_DIRECTORY + File.separator + "IGRA_"+(int)((TimeUnit.MILLISECONDS.toHours(currentTimeInMillis)%24)+2)+"_"+(int)((currentTimeInMillis/(1000*60))%60)+"_"+(int)((currentTimeInMillis/1000)%60)+".txt",true);
        PrintWriter out = new PrintWriter(fw);
        for (Player player : playersList)
        {
            out.append("\n"+player.getPlayerName()+"\n\n");
            for (Figure figure : player.getFigureList()) {
                out.append(figure.toString());
            }
        }
        long runningTimeInSeconds = simulation.runningTime / 1000;
        long hour = runningTimeInSeconds / 3600;
        long minute = runningTimeInSeconds / 60;
        long second = runningTimeInSeconds % 60;

        out.append("\nVrijeme trajanje igre: "+hour+":"+minute+":"+second);
        out.close();
        fw.close();
    }catch (IOException e){
        Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
    }

    }


    public boolean isIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause){
        this.pause=pause;
    }
}
