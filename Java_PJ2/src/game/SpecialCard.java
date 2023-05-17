package game;

public class SpecialCard extends Card {

    private int holesValue;
    private static String SPECIAL_CARD_PATH="src/images/picture0.png";


    public SpecialCard(int holesValue){
        super(SPECIAL_CARD_PATH);
        this.holesValue=holesValue;

    }

    public int getHolesValue() {
        return holesValue;
    }
}
