package nu.berggame.server;

/**
 * Game player class which contains information game servers can use.
 * All User implementations are obligated to have a reference to this class.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 1.0
 */

public class Player {

    private String name;
    private int score;
    private int data;
    private String alias;
    private boolean isReady;

    // Constructor which takes the name of the player.
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.data = 0;
        this.alias = "";
    }

    // Constructor which takes the name of the player as well as an alias.
    public Player(String name, String alias) {
        this.name = name;
        this.alias = alias;
        this.score = 0;
        this.data = 0;
    }


    // Getter for the name of the player.
    public String getName() {
        return name;
    }

    // 3 Utility methods for the score field (get, set and add).
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void addScore(int amount) {
        this.score += amount;
    }

    // Numeric data value which can keep track of round scoring or something else.
    public int getData() {
        return data;
    }
    public void setData(int data) {
        this.data = data;
    }

    // Getter and setter for the player alias.
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

}
