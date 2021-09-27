package nu.berggame.server;

import nu.berggame.shared.messages.Message;
import nu.berggame.shared.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;

public abstract class Game implements Runnable{

    private final int TIMEOUT_SECONDS = 5;

    private boolean alive;
    private int maxPlayerCount;

    private String instanceName;

    private List<User> players;

    public synchronized boolean isAlive() {
        return this.alive;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }

    public Game(String instanceName, int maxPlayerCount) {
        players = new ArrayList<>();
        this.maxPlayerCount = maxPlayerCount;
        this.instanceName = instanceName;
    }

    @Override
    public void run() {
        alive = true;

        int iteration = 0;
        while(!allPlayersReady()) {
            try {
                Thread.sleep(1000);

                if (iteration == TIMEOUT_SECONDS) {

                    User[] unreadyPlayers = getAllUnreadyPlayers();

                    for (User unreadyPlayer : unreadyPlayers) {
                        gameBroadcast(new ServerMessage(unreadyPlayer.getPlayer().getName() + " is not ready!", false), unreadyPlayers);
                    }
                    iteration = 0;
                }
                iteration++;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        gameBroadcast(new ServerMessage("All players ready, starting game", false));

        runGame();

        closeGame();
        alive = false;
    }

    public abstract void runGame();
    public abstract void refresh();

    public void closeGame() {
        for (User ply : players) {
            ply.closeConnection();
        }

        players.clear();
    }

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

    public synchronized void removePlayer(User player) {
        players.remove(player);
    }

    public int getCurrentPlayerCount() {
        return players.size();
    }

    public void gameBroadcast(Message message) {
        for (User ply : players) {
            ply.printMessage(message);
        }
    }

    public void gameBroadcast(Message message, User excludedPlayer) {
        for (User ply : players) {
            if (ply != excludedPlayer) {
                ply.printMessage(message);
            }
        }
    }

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

    private User[] getAllUnreadyPlayers() {
        List<User> unreadyPlayers = new ArrayList<>();
        for (User ply : players) {
            if (!ply.getReady()) {
                unreadyPlayers.add(ply);
            }
        }

        return unreadyPlayers.toArray(new ClientThread[0]);
    }

    private boolean allPlayersReady() {
        for (User ply : players) {
            if (!ply.getReady()) {
                return false;
            }
        }
        return true;
    }
}
