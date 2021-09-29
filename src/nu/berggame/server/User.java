package nu.berggame.server;

import nu.berggame.shared.messages.Message;

/**
 * Interface for any class that may act as a user/client of the server
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.3
 */

public interface User {

    Player getPlayer();

    boolean getReady();

    void printMessage(Message message);
    String readResponse();

    void closeConnection();

}
