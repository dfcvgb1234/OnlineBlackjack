package nu.berggame.shared.messages;

import nu.berggame.shared.Dice;

public class DiceRollMessage implements Message {

    Dice[] dice;

    public DiceRollMessage(Dice[] dice) {
        this.dice = dice;
    }

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

    @Override
    public String toString() {
        String result = "";
        for (Dice die : dice) {
            result += ";" + die.getSideCount() + ":" + die.getCurrentFace();
        }
        return "drm" + result;
    }
}
