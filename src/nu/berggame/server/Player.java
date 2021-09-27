package nu.berggame.server;

public class Player {

    private String name;
    private int score;
    private int data;
    private String alias;
    private boolean isReady;

    public Player(String name) {
        this.name = name;
        score = 0;
        data = 0;
        alias = "";
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public void addScore(int amount) {
        this.score += amount;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
