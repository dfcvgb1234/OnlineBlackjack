package nu.berggame.server;

import nu.berggame.shared.messages.Message;

/**
 * Used to create a local client with no mass, this does not need any information except the player object.
 * This is useful for creating computer controlled players.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.1
 */

public class DummyClient implements User {

    private Player player;

    public DummyClient(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean getReady() {
        return false;
    }

    public void printMessage(Message message) {

    }

    public String readResponse() {
        return "";
    }

    public void closeConnection() {

    }
}