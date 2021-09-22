package com.company.server;

import com.company.shared.messages.Message;

public interface Game {

    boolean addPlayer(User player);
    void removePlayer(User player);

    void gameBroadcast(Message message);
    void gameBroadcast(Message message, User excludedUser);
    void gameBroadcast(Message message, User[] excludedUsers);

    String getInstanceName();
}
