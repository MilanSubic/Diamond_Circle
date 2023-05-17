package game;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static game.Main.simulation;

public class SettingsSceneController {


    public TextField dimensionOfMatrixField;
    public TextField numberOfPlayersField;
    public Button settingsButton;


    public void settingsButtonAction() throws IOException {

        try {
            simulation = new Simulation();  // kreiranje simulacije
            try{
                checkDimensionValue();
            }catch (WrongDataException e){
                Logger.getLogger(Simulation.class.getName()).log(Level.WARNING,e.fillInStackTrace().toString());
            }
            checkNumberOfPlayersValue();
            simulation.createPlayers();  // kreiranje igraca sa figurama i odgovarajucim bojama
            simulation.createCards();// kreiranje spila karata koji je izmijesan
            simulation.choosePathFromDimension(); //odgovarajuca putanja izabrana iz fajla na osnovu dimenzije matrice
            changeToMainScene();
        }catch(WrongDataException e){
            Logger.getLogger(Simulation.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
        }


    }

    private void changeToMainScene() throws IOException {
        Parent secondPage = FXMLLoader.load(getClass().getResource("gameScene.fxml"));
        Stage window = (Stage) settingsButton.getScene().getWindow();
        window.setScene(new Scene(secondPage, 600, 600));



    }


    private void checkDimensionValue() throws WrongDataException {
        int tempValue=Integer.parseInt(dimensionOfMatrixField.getText());
        if(tempValue>=7 && tempValue<=10) {
            simulation.setDimensionOfMatrix(tempValue);
            simulation.matrix = new Object[tempValue][tempValue];
        }
        else {
            throw new WrongDataException("Dimenzija nije odgovarajuća!");
        }
    }

    private void checkNumberOfPlayersValue() throws WrongDataException {
        int tempValue=Integer.parseInt(numberOfPlayersField.getText());
        if(tempValue>=2 && tempValue<=4)
            simulation.setNumberOfPlayers(tempValue);
        else {
            throw new WrongDataException("Broj igrača nije odgovarajući!");
        }
    }
}

