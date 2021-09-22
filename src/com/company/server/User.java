package com.company.server;

import com.company.shared.messages.Message;

public interface User {

    Player getPlayer();

    boolean getReady();

    void printMessage(Message message);
    String readResponse();

    void closeConnection();

}
