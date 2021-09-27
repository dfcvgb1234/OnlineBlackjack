package nu.berggame.server;

import nu.berggame.server.games.BlackjackCardGame;
import nu.berggame.server.games.BlackjackDiceGame;
import nu.berggame.shared.messages.ServerMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GameHost {

    private Hashtable<String, Game[]> games;

    private List<ClientThread> clients;
    private ServerSocket socket;

    private final int MAX_PLAYER_COUNT = 3;
    private final int TARGET_SCORE = 10;
    private final int MAX_GAME_INSTANCES = 2;

    public void start(int port)
    {
        games = new Hashtable<>();

        clients = new ArrayList<>();
        try {
            socket = new ServerSocket(port);

            BlackjackDiceGame[] blackjackDiceGames = new BlackjackDiceGame[MAX_GAME_INSTANCES];
            BlackjackCardGame[] blackjackCardGames = new BlackjackCardGame[MAX_GAME_INSTANCES];

            for (int i = 0; i < MAX_GAME_INSTANCES; i++) {
                blackjackDiceGames[i] = new BlackjackDiceGame("Blackjack Dice Server " + (i+1), MAX_PLAYER_COUNT, TARGET_SCORE);
                blackjackCardGames[i] = new BlackjackCardGame("Blackjack Card Server " + (i+1), MAX_PLAYER_COUNT, TARGET_SCORE);
            }

            games.put("Blackjack Card Game", blackjackCardGames);
            games.put("Blackjack Dice Game", blackjackDiceGames);

            System.out.println("GameHost server started!");

            while (true) {
                Socket client = socket.accept();
                ClientThread player = new ClientThread(client, this);

                refreshAvailableGames(games);

                clients.add(player);
                player.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getGameList() {
        String[] list = new String[games.size()];

        Enumeration<String> enumeration = games.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            list[i] = enumeration.nextElement();
            i++;
        }
        return list;
    }

    private void refreshAvailableGames(Hashtable<String, Game[]> games) {
        for (Game[] gameList : games.values()) {
            for (Game game : gameList) {
                if (!game.isAlive() && game.getCurrentPlayerCount() == 0) {
                    System.out.println(game.getInstanceName() + " has been refreshed");
                    game.refresh();
                }
            }
        }
    }

    public boolean findNextAvailableServer(ClientThread player, String gameType) {
        if (games.containsKey(gameType)) {
            Game[] gameList = games.get(gameType);
            for (Game game : gameList) {
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
