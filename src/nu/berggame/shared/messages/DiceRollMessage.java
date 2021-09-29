package nu.berggame.shared.messages;

import nu.berggame.shared.Dice;

/**
 * Represents a message that tells the client it should render dice roll.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.8
 */

public class DiceRollMessage implements Message {

    Dice[] dice;

    // Constructor which takes an array of dice it sends to the client for render.
    public DiceRollMessage(Dice[] dice) {
        this.dice = dice;
    }

    // Constructor which takes a rawString from the server which it parses into useful information.
    public DiceRollMessage(String rawString) {
        String[] diceInfo = rawString.split(";");

        Dice[] diceResult = new Dice[diceInfo.length-1];
        for (int i = 1; i < diceInfo.length; i++) {
            Dice tempDice = new Dice(Integer.parseInt(diceInfo[i].split(":")[0]));
            tempDice.setCurrentFace(Integer.parseInt(diceInfo[i].split(":")[1]));
            diceResult[i-1] = tempDice;
        }

        this.dice = diceResult;
    }


    public Dice[] getDice() {
        return this.dice;
    }

    // Turns this object into a string representation, which can later be parsed by the same class on the client.
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Dice die : dice) {
            result.append(";").append(die.getSideCount()).append(":").append(die.getCurrentFace()); // A separated string is used, so we easily can split the string into useful information once it reaches the client.
        }
        return "drm" + result;
    }
}
