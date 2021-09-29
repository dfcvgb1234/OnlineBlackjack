package nu.berggame.server;

import nu.berggame.server.games.BlackjackCardGame;
import nu.berggame.server.games.BlackjackDiceGame;
import nu.berggame.shared.messages.ServerMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Keeps track of all hosted games, and refreshes completed games.
 * This class is optimized for easy implementation of a new game.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 1.2
 */

public class GameHost {

    private Hashtable<String, Game[]> games;

    private List<ClientThread> clients;
    private ServerSocket socket;

    // These are the settings for all game instances, however this can independently be overwritten with each game instance.
    private final int MAX_PLAYER_COUNT = 3;
    private final int TARGET_SCORE = 10;
    private final int MAX_GAME_INSTANCES = 2;

    // Starts a game host on the specified port.
    public void start(int port)
    {

        games = new Hashtable<>(); // Keeps track of all games.

        clients = new ArrayList<>(); // Keeps track of all clients on all games.

        try {
            socket = new ServerSocket(port);

            BlackjackDiceGame[] blackjackDiceGames = new BlackjackDiceGame[MAX_GAME_INSTANCES];
            BlackjackCardGame[] blackjackCardGames = new BlackjackCardGame[MAX_GAME_INSTANCES];

            // Create instances with default settings.
            for (int i = 0; i < MAX_GAME_INSTANCES; i++) {
                blackjackDiceGames[i] = new BlackjackDiceGame("Blackjack Dice Server " + (i+1), MAX_PLAYER_COUNT, TARGET_SCORE);
                blackjackCardGames[i] = new BlackjackCardGame("Blackjack Card Server " + (i+1), MAX_PLAYER_COUNT, TARGET_SCORE);
            }

            games.put("Blackjack Card Game", blackjackCardGames);
            games.put("Blackjack Dice Game", blackjackDiceGames);

            System.out.println("GameHost server started!");

            while (true) {
                Socket client = socket.accept(); // socket.accept() is a blocking method, this is why we are creating a new thread when a player joins.
                ClientThread player = new ClientThread(client, this); // Create a new ClientThread object with the information from the newly connected player.

                refreshAvailableGames(games); // Refresh all games, so completed games can be started again.

                clients.add(player); // Add the player to the list of clients connected.
                player.start(); // Start the client thread.
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns a list of all game types
    public String[] getGameList() {
        String[] list = new String[games.size()];

        Enumeration<String> enumeration = games.keys();
        int i = 0;
        while (enumeration.hasMoreElements()) { // Enumerate through the list of keys in the Hashtable.
            list[i] = enumeration.nextElement();
            i++;
        }
        return list;
    }

    // Refreshes finished games, so they can be started again.
    private void refreshAvailableGames(Hashtable<String, Game[]> games) {
        for (Game[] gameList : games.values()) {
            for (Game game : gameList) {
                if (!game.isAlive() && game.getCurrentPlayerCount() == 0) { // Only refresh the game if it is stopped and no players are connected.
                    game.refresh();
                    System.out.println(game.getInstanceName() + " has been refreshed");
                }
            }
        }
    }

    // Find the next available server based on the wanted game type.
    public boolean findNextAvailableServer(ClientThread player, String gameType) {
        if (games.containsKey(gameType)) {
            Game[] gameList = games.get(gameType);
            for (Game game : gameList) {
                if (game.addPlayer(player)) {
                    player.setCurrentGameInstance(game);

                    if (!game.isAlive()) { // If it is the first player to joined, start the game instance.
                        Thread thread = new Thread(game);
                        thread.start();
                    }

                    return true;
                }
                else {
                    System.out.println(game.getInstanceName() + " is full!"); // Logs when a server is full and a player tried to join.
                }
            }
        }
        return false; // If no servers were found return false;
    }

    // Called if all games should close and all players should disconnect
    public void closeServer() {
        try {
            for (Game[] gameList : games.values()) {
                for (Game game : gameList) {
                    game.closeGame();
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sends a message to all players on all servers.
    public void broadcast(ServerMessage message)
    {
        for (ClientThread thread : clients)
        {
            thread.printMessage(message);
        }
    }
}
