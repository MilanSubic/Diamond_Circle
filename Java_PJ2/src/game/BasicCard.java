package game;

public class BasicCard extends Card {

    private static String BASIC_CARD_PATH="src/images/";
    private int cardValue;


    public BasicCard(int cardValue){

        super(BASIC_CARD_PATH+"picture"+cardValue+".png");
        this.cardValue=cardValue;

    }

    public int getCardValue(){
        return cardValue;
    }
}
