package game;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import static game.Main.simulation;

public class ResultSceneController {


    public TextArea textArea;
    @FXML
    public void initialize(){

        textArea.setText(simulation.resultStringBuilder.toString());
    }


}
