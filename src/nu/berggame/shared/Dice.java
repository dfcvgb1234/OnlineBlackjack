package nu.berggame.shared;

import nu.berggame.shared.messages.DiceRollMessage;
import nu.berggame.shared.messages.Message;

import java.util.Random;

/**
 * Represents a single die with a predefined side count
 *
 * @author  Sebastian Berg Rasmussen
 * @version 1.2
 */

public class Dice implements Component{

    private int sideCount;
    private int currentFace;

    // Creates a new dice with a specified side count.
    public Dice (int sideCount) {
        this.sideCount = sideCount;
        this.currentFace = 1;
    }

    // Rolls the die and returns the side it landed on.
    public int roll() {
        Random rand = new Random();
        this.currentFace = rand.nextInt(sideCount)+1;
        return this.currentFace;
    }

    public int getSideCount() {
        return this.sideCount;
    }

    // Every time the die is thrown the current face is saved, this returns it.
    public int getCurrentFace() {
        return this.currentFace;
    }

    // Set the current face.
    public void setCurrentFace(int face) {
        if (face <= this.sideCount && face >= 1) {
            this.currentFace = face;
        }
        else {
            // If the face argument is out of range of the die side count, throw an error.
            throw new IllegalArgumentException("You can not set a face larger than side count for a dice or smaller than 1 ");
        }
    }
}
