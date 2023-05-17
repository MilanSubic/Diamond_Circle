package game;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import static game.Main.simulation;
public class BasicFigure extends Figure{


    public BasicFigure(Color color) {
        super(color);
    }
    public BasicFigure(Color color,int numberOfJumps){
        super(color,numberOfJumps);
    }

    @Override
    public String toString(){
        int temp=this.getRoadLengthOfFigure();
        String color="";
        String figureEnd="";
        List tempList=new ArrayList();
        for(int i=0;i<temp;i++)
        {
            tempList.add(simulation.diamondCirclePath.get(i));
        }
        switch (this.getColor().toString())
        {
            case "0x008000ff": color="zelena";break;
            case "0xffff00ff": color="zuta";break;
            case "0xff0000ff": color="crvena";break;
            case "0x0000ffff": color="plava";break;


        }
        if(this.getRoadLengthOfFigure()==simulation.diamondCirclePath.size())
        {
            figureEnd="DA";
        }
        else figureEnd="NE";

        return this.getFigureName()+" ( basic figure, "+color+" )"+" Predjeni put: "+  tempList.toString()+" Figura završila kretanje: "+figureEnd+"\n";
    }
}
