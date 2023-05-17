package game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


import java.util.logging.Level;
import java.util.logging.Logger;

import static game.Main.simulation;

public class RoadOfFigureController {

    public Label label;
    public StackPane stackPane;

    public GridPane gridPane;
    public Label timeLabel;


    @FXML
    public void initialize() {

        gridPane=createMatrixGrid();
        stackPane.getChildren().add(gridPane);

        new Thread(()->
        {
            for(Player player: simulation.playersList) {
                for (Figure figure : player.getFigureList()) {

                    if (figure.getFigureId() == simulation.indexOfPressedButton) {

                        while(simulation.isIsRunning()) {

                            Platform.runLater(() -> {

                                showRoadOfFigure(figure);

                            });

                            try {
                                Thread.sleep(Simulation.SECOND_SLEEP_TIME);
                            } catch (InterruptedException e) {
                                Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
                            }
                        }
                    }
                }
            }
        }).start();


    }



    private void showRoadOfFigure(Figure figure){

        if (figure.isStarted()) {

            label.setText("             "+figure.getFigureName());
            timeLabel.setText((figure.timeOfMovingFigure / 1000) / 60 + ":" + (figure.timeOfMovingFigure / 1000) % 60);
            for (int value = 0; value < figure.figurePath.size(); value++) {

                int indexOfFigure = simulation.diamondCirclePath.get(value) - 1;
                int x = indexOfFigure / simulation.getDimensionOfMatrix();
                int y = indexOfFigure % simulation.getDimensionOfMatrix();
                StackPane stackPane = new StackPane();
                stackPane.setBackground(new Background(new BackgroundFill(figure.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
                gridPane.add(stackPane, y, x);
            }

        }

        else
        {
            label.setText("Figura nije startovana!");
        }
    }




    private GridPane createMatrixGrid() {

        GridPane gridPane=new GridPane();
        for(int i=0;i<simulation.getDimensionOfMatrix();i++)
            for(int j=0;j<simulation.getDimensionOfMatrix();j++)
            {
                StackPane tempField=new StackPane();
                tempField.setAlignment(Pos.CENTER);
                tempField.setPrefHeight(40);
                tempField.setPrefWidth(40);
                tempField.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.2))));
                gridPane.add(tempField,j,i);

            }
        return gridPane;
    }



}
