package game;

import javafx.scene.image.Image;

import java.io.File;

public class Hole {

    private String path;
    private static String HOLE_PATH="src/images/hole.png";


    public Hole(){

        this.path=HOLE_PATH;

    }

    public Image getImageOfHole(){

        return new Image(new File(path).toURI().toString(),20,20,false,false);
    }


}
