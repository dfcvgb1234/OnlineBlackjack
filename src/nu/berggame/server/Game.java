package nu.berggame.server;

import nu.berggame.shared.messages.Message;
import nu.berggame.shared.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class which is useful for when creating playable games.
 * Extending this class grants a lot of useful functionality, like checking if all players are ready and threading.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.2
 */

public abstract class Game implements Runnable{

    private final int TIMEOUT_SECONDS = 5;

    private boolean alive;
    private int maxPlayerCount;
    private String instanceName;
    private List<User> players;

    // Public getter for alive, this keeps track of the thread, if the thread has completed its cycle it sets alive to false.
    // Synchronized makes the method thread-safe.
    public synchronized boolean isAlive() {
        return this.alive;
    }

    // Public getter for the instance name, this is used to know the difference between all instances created by the GameHost.
    public String getInstanceName() {
        return this.instanceName;
    }

    // Public getter and setter for the max player count.
    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }

    // Gets a list of all connected players to this instance.
    public List<User> getPlayers() {
        return players;
    }

    // Constructor which takes an instance name and a max player count, this is only useful if super() is called on it.
    public Game(String instanceName, int maxPlayerCount) {
        players = new ArrayList<>();
        this.maxPlayerCount = maxPlayerCount;
        this.instanceName = instanceName;
    }

    // Override for the runnable run method, this is run when the runnable is started through a thread object.
    @Override
    public void run() {
        alive = true;

        int iteration = 0;
        while(!allPlayersReady()) { // Keeps checking if all players are ready.
            try {
                Thread.sleep(1000);

                if (iteration == TIMEOUT_SECONDS) { // To prevent long standby times for checking if all players are ready, the thread is only sleeping for 1 second.
                                                    // But to prevent mass spamming of unready players a timeout is set in place.

                    User[] unreadyPlayers = getAllUnreadyPlayers();

                    for (User unreadyPlayer : unreadyPlayers) {
                        // Sends a message to the game instance with the names of unready players.
                        gameBroadcast(new ServerMessage(unreadyPlayer.getPlayer().getName() + " is not ready!", false), unreadyPlayers);
                    }
                    iteration = 0;
                }
                iteration++;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        gameBroadcast(new ServerMessage("All players ready, starting game", false)); // Notify all players that the game is starting.

        runGame(); // Call the abstract method which games should have implemented.

        closeGame(); // When the game is complete close the game instance.
        alive = false;
    }

    public abstract void runGame();
    public abstract void refresh();

    public void closeGame() {
        for (User ply : players) {
            ply.closeConnection(); // When closing the game, call closeConnection on all players.
        }

        players.clear(); // Remove all players from the current game instance.
    }

    // Add a player to the server, returns true if it was successful, if the server is full this method returns false.
    // If the game is started players are also prevented from joining.
    public synchronized boolean addPlayer(User player) {
        if (isAlive()) {
            return false;
        }
        if (players.size() == maxPlayerCount) {
            return false;
        } else {
            players.add(player);
            return true;
        }
    }

    // Removes a player from the game.
    public synchronized void removePlayer(User player) {
        players.remove(player);
    }

    // Get how many players currently have joined the game.
    public int getCurrentPlayerCount() {
        return players.size();
    }

    // Broadcast a message to all players of this instance.
    public void gameBroadcast(Message message) {
        for (User ply : players) {
            ply.printMessage(message);
        }
    }

    // Broadcast a message to all players of this instance except the excluded player.
    public void gameBroadcast(Message message, User excludedPlayer) {
        for (User ply : players) {
            if (ply != excludedPlayer) {
                ply.printMessage(message);
            }
        }
    }

    // Broadcast a message to all players of the instance except the excluded players.
    public void gameBroadcast(Message message, User[] excludedPlayers) {
        for (User player : players) {
            boolean excluded = false;
            for (User exPlayer : excludedPlayers) {
                if (player == exPlayer) {
                    excluded = true;
                    break;
                }
            }

            if (!excluded) {
                player.printMessage(message);
            }
        }
    }

    // Gets a list of all the currently connected players that are not yet ready.
    private User[] getAllUnreadyPlayers() {
        List<User> unreadyPlayers = new ArrayList<>();
        for (User ply : players) {
            if (ply.getReady()) {
                unreadyPlayers.add(ply);
            }
        }

        return unreadyPlayers.toArray(new User[0]);
    }

    // Checks if all players are ready.
    private boolean allPlayersReady() {
        for (User ply : players) {
            if (ply.getReady()) {
                return false;
            }
        }
        return true;
    }
}
