package nu.berggame.server;

import nu.berggame.shared.messages.Message;

public class DummyClient implements User {

    private Player player;

    public DummyClient(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean getReady() {
        return true;
    }

    public void printMessage(Message message) {

    }

    public String readResponse() {
        return "";
    }

    public void closeConnection() {

    }
}