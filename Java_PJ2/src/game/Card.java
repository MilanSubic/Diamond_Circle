package game;

import javafx.scene.image.Image;

import java.io.File;

public abstract class Card {

    private String path;

    public Card(String path){
        this.path=path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Image getImageOfCard(){
        return  new Image(new File(path).toURI().toString(), 120, 250, false, false);

    }
}
