package com.company.server;

import com.company.shared.messages.ServerMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BlackjackServer {

    private List<ClientThread> clients;
    private ServerSocket socket;

    private final int MAX_PLAYER_COUNT = 3;
    private final int TARGET_SCORE = 10;
    private final int MAX_GAME_INSTANCES = 2;

    public void start(int port)
    {

        clients = new ArrayList<>();
        try {
            socket = new ServerSocket(port);

            BlackjackGame[] games = new BlackjackGame[MAX_GAME_INSTANCES];

            for (int i = 0; i < MAX_GAME_INSTANCES; i++) {
                games[i] = new BlackjackGame("Blackjack Server " + (i+1), MAX_PLAYER_COUNT, TARGET_SCORE);
            }

            System.out.println("Blackjack server started!");

            while (true) {
                Socket client = socket.accept();
                ClientThread player = new ClientThread(client, this);

                refreshAvailableGames(games);

                if (!findNextAvailableServer(player, games))
                {
                    System.out.println("No free servers to join!");
                    player.printMessage(new ServerMessage("No free servers to join!", false, true));
                }
                else {
                    clients.add(player);
                    player.start();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshAvailableGames(BlackjackGame[] games) {
        for (BlackjackGame game : games) {
            if (!game.isAlive() && game.getPlayerCount() == 0) {
                System.out.println(game.getInstanceName() + " has been refreshed");
                game = new BlackjackGame(game.getInstanceName(), MAX_PLAYER_COUNT, TARGET_SCORE);
            }
        }
    }

    private boolean findNextAvailableServer(ClientThread player, BlackjackGame[] games) {
        for (BlackjackGame game : games) {
            if (game.addPlayer(player)) {
                player.setCurrentGameInstance(game);
                if (!game.isAlive()) {
                    Thread thread = new Thread(game);
                    thread.start();
                }

                return true;
            }
            else {
                System.out.println(game.getInstanceName() + " is full!");
            }
        }
        return false;
    }

    public void closeServer()
    {
        try {
            for (ClientThread thread : clients)
            {
                thread.closeConnection();
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean getAllPlayersReady()
    {
        for (ClientThread thread : clients)
        {
            if (!thread.getReady())
            {
                return false;
            }
        }
        return true;
    }

    public void broadcast(ServerMessage message)
    {
        for (ClientThread thread : clients)
        {
            thread.printMessage(message);
        }
    }
}
