package game;

import javafx.scene.image.Image;

import java.io.File;

public class Diamond {

    public static String DIAMOND_PATH="src/images/diamond.png";
    public String path;

    public Diamond(){
        this.path=DIAMOND_PATH;
    }

    public Image getImageOfDiamond(){
        return  new Image(new File(path).toURI().toString(), 20, 20, false, false);

    }

}
