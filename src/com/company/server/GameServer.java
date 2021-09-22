package com.company.server;

public class GameServer {

    public static void main(String[] args) {
        System.out.println("Starting blackjack server!");
        BlackjackServer server = new BlackjackServer();
        server.start(6060);
    }
}
