package nu.berggame.server;

import nu.berggame.shared.messages.Message;

public interface User {

    Player getPlayer();

    boolean getReady();

    void printMessage(Message message);
    String readResponse();

    void closeConnection();

}
