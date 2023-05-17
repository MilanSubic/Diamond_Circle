package game;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static game.Main.simulation;

public class GameSceneController {

    public Button startButton;
    public StackPane stackPane;
    public ImageView imageView;
    public TextArea textArea;
    public VBox vBox;
    public GridPane gridPane;
    public HBox hBox;
    public Label timeLabel;
    public ListView listView;
    public Label resultLabel;
    public Button newGameButton;
    private int dimensionOfMatrix;
    private StackPane[][] matrixFields;

    public static String PATH_OF_BASIC_FIGURE_IMAGE="src/images/obicna.png";
    public static String PATH_OF_FLAYING_FIGURE_IMAGE="src/images/lebdeca.png";
    public static String PATH_OF_FAST_FIGURE_IMAGE="src/images/super_brza.png";



@FXML
    public void initialize(){
    dimensionOfMatrix=simulation.getDimensionOfMatrix();
    matrixFields=new StackPane[dimensionOfMatrix][dimensionOfMatrix];
    gridPane=createMatrixGrid();
    stackPane.getChildren().add(gridPane);
    createFiguresButtonList();
    createPlayerLabels();
    showResultFiles();
    newGameButton.setDisable(true);


}

    public GameSceneController(){}

    public GridPane createMatrixGrid() {

    GridPane gridPane=new GridPane();
        int fieldCounter=1;
        for(int i=0;i<dimensionOfMatrix;i++)
            for(int j=0;j<dimensionOfMatrix;j++)
            {
                Label label=new Label(String.valueOf(fieldCounter++));
                label.setFont(new Font(10));
                label.setPadding(new Insets(0,10,0,0));
                StackPane tempField=new StackPane(label);
                tempField.setAlignment(Pos.CENTER);
                tempField.setPrefHeight(40);
                tempField.setPrefWidth(40);
                tempField.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.2))));
                gridPane.add(tempField,j,i);
                matrixFields[i][j]=tempField;

            }
        return gridPane;
    }

    public void createFiguresButtonList(){

        simulation.figureButtonsList = new Button[simulation.getNumberOfPlayers()*4];
        for(int i=0;i<simulation.getNumberOfPlayers()*4;i++)
        {

            simulation.figureButtonsList[i] = new Button();
            simulation.figureButtonsList[i].setPrefHeight(20);
            simulation.figureButtonsList[i].setPrefWidth(80);
            simulation.figureButtonsList[i].setAlignment(Pos.CENTER);
            simulation.figureButtonsList[i].setText("Figure " + (i + 1));
            simulation.figureButtonsList[i].setId("button"+i);
        }
        vBox.getChildren().addAll(simulation.figureButtonsList);
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setPadding(new Insets(0, 0, 0, 0));

        for(int k=0;k<simulation.figureButtonsList.length;k++) {

            int temp = k;


            simulation.figureButtonsList[k].setOnAction(event->{

                try {
                    simulation.indexOfPressedButton=temp;
                    changeToFigureScene();
                } catch (IOException e) {
                    Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
                }

            });

        }

    }

    public void createPlayerLabels(){
        for(Player player:simulation.playersList)
        {
            Label label=new Label();
            label.setText(player.getPlayerName());
            label.setTextFill(player.getFigureList().get(0).getColor());
            label.setPadding(new Insets(0,30,0,30));
            hBox.getChildren().add(label);
        }
    }

    public void writeTextArea(){

        textArea.setText("Na potezu je igrač ---------- "+simulation.currentPlayer.getPlayerName()+"\n"
                +simulation.currentPlayer.currentFigure.getFigureName()+"   prelazi   "+simulation.currentPlayer.currentFigure.getNumberOfJumps()+"   polja.\n"+
                "Pomjera se sa polja    "+simulation.currentPlayer.currentFigure.figureStartIndex+"   na polje   "+simulation.currentPlayer.currentFigure.figureEndIndex);
    }

    public void showResultFiles(){

        File directoryPath=new File(Simulation.PATH_OF_RESULT_DIRECTORY);
        ObservableList<String> listForListView = FXCollections.observableArrayList();
        File[] resultFiles=directoryPath.listFiles();
        resultLabel.setText(String.valueOf(resultFiles.length));
        for(File file:resultFiles)
        {
            listForListView.add(file.getName());
        }
        listView.setItems(listForListView);

    }



    public void showResultInTextArea(){

        simulation.resultStringBuilder.setLength(0);
        File resultFile=new File(Simulation.PATH_OF_RESULT_DIRECTORY + File.separator + listView.getSelectionModel().getSelectedItem());
        try{
            BufferedReader bf=new BufferedReader(new FileReader(resultFile));
            String line=bf.readLine();
            while(line!=null)
            {
                    simulation.resultStringBuilder.append(line+"\n");
                    line=bf.readLine();

            }
            bf.close();

            moveToResultScene();


        }catch (IOException e){
            Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
        }

    }

    public void startButtonAction() {


    if(simulation.counter % 2 == 0)
    {
        if(simulation.counter==0) {


            simulationThread.start();
            figureShowThread.start();
            simulationRunnningTime.start();

        }
        synchronized (simulation.lock) {
            simulation.setPause(false);
            simulation.lock.notify();
        }

    }else
    {
        simulation.setPause(true);

    }
        simulation.counter++;

    }

    Thread simulationThread=new Thread(() -> {
        try {
            simulation.startGame();
        } catch (InterruptedException | IOException e) {
            Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
        }
    });

    Thread simulationRunnningTime=new Thread(()->{

        long hour=0,minute=0,second=0;
        long startSimulationTime=System.currentTimeMillis();
        long pauseTimeInMillis=0;
        while(simulation.isIsRunning()) {
            if (!simulation.isPause()) {
                simulation.runningTime = System.currentTimeMillis() - (startSimulationTime + pauseTimeInMillis);
                long runningTimeInSeconds = simulation.runningTime / 1000;
                hour = runningTimeInSeconds / 3600;
                minute = runningTimeInSeconds / 60;
                second = runningTimeInSeconds % 60;

                long finalHour = hour;
                long finalMinute = minute;
                long finalSecond = second;

                Platform.runLater(() -> {

                    timeLabel.setText("Time: " + finalHour + ":" + finalMinute + ":" + finalSecond);


                });
            }else {
                pauseTimeInMillis+=1000;
            }
            try {
                Thread.sleep(Simulation.SECOND_SLEEP_TIME);
            } catch (InterruptedException e) {
                Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
            }
        }

        Platform.runLater(this::showResultFiles);

    });


    Thread figureShowThread=new Thread(()-> {
        while (simulation.isIsRunning()) {
            if(!simulation.isPause()) {

                Platform.runLater(() -> {

                    List previousElementsOnGridList = gridPane.getChildren().stream().filter((node) -> node instanceof ImageView || node instanceof Rectangle).collect(Collectors.toList());
                    gridPane.getChildren().removeAll(previousElementsOnGridList);

                    try {
                        if(simulation.currentCard!=null)
                            imageView.setImage(simulation.currentCard.getImageOfCard());
                        if (simulation.currentPlayer!= null) {
                            if(simulation.currentPlayer.currentFigure!=null)
                                writeTextArea();
                        }
                    }catch(NullPointerException e){
                        Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
                    }
                    for (int k = 0; k < dimensionOfMatrix; k++)
                        for (int t = 0; t < dimensionOfMatrix; t++) {
                            Object temp = simulation.matrix[k][t];
                            if (temp instanceof Diamond && simulation.runningTime>Simulation.GHOST_SLEEP_TIME) {
                                gridPane.add(new ImageView(new Diamond().getImageOfDiamond()), t, k);
                            }
                            Rectangle rectangle = new Rectangle(3, 20);
                            if (temp instanceof FastFigure) {

                                rectangle.setFill(((FastFigure) temp).getColor());
                                gridPane.add(new ImageView(new Image(new File(PATH_OF_FAST_FIGURE_IMAGE).toURI().toString(), 20, 20, false, false)), t, k);
                                gridPane.add(rectangle, t, k);
                            }
                            if (temp instanceof BasicFigure) {

                                rectangle.setFill(((BasicFigure) temp).getColor());
                                gridPane.add(new ImageView(new Image(new File(PATH_OF_BASIC_FIGURE_IMAGE).toURI().toString(), 20, 20, false, false)), t, k);
                                gridPane.add(rectangle, t, k);
                            }


                            if (temp instanceof FlyingFigure) {

                                rectangle.setFill(((FlyingFigure) temp).getColor());
                                gridPane.add(new ImageView(new Image(new File(PATH_OF_FLAYING_FIGURE_IMAGE).toURI().toString(), 20, 20, false, false)), t, k);
                                gridPane.add(rectangle, t, k);
                            }
                            int indexOfHole;
                            for(int i=0;i<simulation.holes.size();i++)
                            {
                                indexOfHole=simulation.holes.get(i)-1;
                                int holeX = indexOfHole / dimensionOfMatrix;
                                int holeY = indexOfHole % dimensionOfMatrix;
                                gridPane.add(new ImageView(new Hole().getImageOfHole()), holeY, holeX);
                            }

                        }


                });
            }

                try {
                    Thread.sleep(Simulation.SECOND_SLEEP_TIME /2);
                } catch (InterruptedException e) {
                    Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
                }


        }
        newGameButton.setDisable(false);
    });




    private void changeToFigureScene() throws IOException {
        Parent secondPage = FXMLLoader.load(getClass().getResource("roadOfFigureScene.fxml"));
        Stage window=new Stage();
        window.setScene(new Scene(secondPage, 600, 400));
        window.show();


    }
    private void moveToResultScene() throws IOException {
        Parent secondPage = FXMLLoader.load(getClass().getResource("resultScene.fxml"));
        Stage window=new Stage();
        window.setScene(new Scene(secondPage, 600, 400));
        window.show();

    }


    public void newGameButtonAction() throws IOException {


        Parent secondPage = FXMLLoader.load(getClass().getResource("settingsScene.fxml"));
        Stage window = (Stage) newGameButton.getScene().getWindow();
        window.setScene(new Scene(secondPage, 400, 400));
    }
}
