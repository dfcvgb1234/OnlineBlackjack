package nu.berggame.shared;

import nu.berggame.shared.messages.DiceRollMessage;
import nu.berggame.shared.messages.Message;

import java.util.Random;

public class Dice implements Component{

    private int sideCount;
    private int currentFace;

    public Dice (int sideCount) {
        this.sideCount = sideCount;
        this.currentFace = 1;
    }

    public int roll() {
        Random rand = new Random();
        this.currentFace = rand.nextInt(sideCount)+1;
        return this.currentFace;
    }

    public int getSideCount() {
        return this.sideCount;
    }

    public int getCurrentFace() {
        return this.currentFace;
    }

    public void setCurrentFace(int face) {
        if (face <= this.sideCount) {
            this.currentFace = face;
        }
        else {
            throw new IllegalArgumentException("You can not set a face larger than side count for a dice");
        }
    }

    public static Class<? extends Message> getMessageType() {
        return DiceRollMessage.class;
    }
}
