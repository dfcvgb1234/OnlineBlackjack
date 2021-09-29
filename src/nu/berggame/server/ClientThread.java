package nu.berggame.server;

import nu.berggame.shared.messages.Message;
import nu.berggame.shared.messages.ServerMessage;

import java.io.*;
import java.net.*;

/**
 * Thread that is created everytime a user joins, this is a thread to prevent locking when input is expected.
 * When this class is created the user is prompted a list of possible games to join.
 * After a game is selected and a server is found, the user is asked to write OK when they are ready to play.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.4
 */

public class ClientThread extends Thread implements User {

    private boolean isReady;
    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;
    private GameHost server;
    private Player player;

    private Game currentGameInstance;

    public ClientThread(Socket socket, GameHost server) {
        this.socket = socket;
        this.server = server;
        this.isReady = false;

        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // This is called when the thread should run.
    @Override
    public void run() {
        super.run();
        try {

            InputStream input = socket.getInputStream(); // Get the input stream from the reader, this reads all messages from the client.
            reader = new BufferedReader(new InputStreamReader(input)); // Creates a buffered stream reader, to read the socket input stream.

            String playerName = reader.readLine(); // Expect the client to send their name when they join the server.
            System.out.println("A player joined: " + playerName); // Write in the server log, that a new player joined the server.

            Player ply = new Player(playerName); // Create a new player object, that games can use to keep track of score.
            this.player = ply;

            String selectedGame = ""; // Keeps track of the selected game key.
            boolean gameSelected = false;
            while (!gameSelected) {

                printMessage(new ServerMessage("Game list:", false));
                String[] gameList = server.getGameList(); // Gets the game list from the GameHost class.
                for (int i = 0; i < gameList.length; i++) {
                    printMessage(new ServerMessage((i+1) + ". " + gameList[i], false));
                }

                printMessage(new ServerMessage("", false));
                printMessage(new ServerMessage("Please select a game: ", true));
                String message = reader.readLine(); // Read the input from the client.

                try {
                    int selectedGameIndex = Integer.parseInt(message)-1;
                    selectedGame = gameList[selectedGameIndex]; // As we have a try surrounding this statement, we do not need to worry about checking if the index matches.
                    gameSelected = true;
                    printMessage(new ServerMessage("", false));
                    printMessage(new ServerMessage("looking for game...", false));

                }
                catch (Exception ex) {
                    printMessage(new ServerMessage("Please enter a numerical value between 1 and " + gameList.length, false));
                    printMessage(new ServerMessage("", false));
                    gameSelected = false;
                }
            }

            // Makes the GameHost find the next available server, if no servers are found the player is disconnected.
            if (!server.findNextAvailableServer(this, selectedGame)) {
                printMessage(new ServerMessage("No open servers found!", false));
                this.closeConnection();
                return;
            }
            else { // If a server is found, write it in the server log and notify the player.
                System.out.println(this.player.getName() + " just joined: " + getCurrentGameInstance().getInstanceName());
                printMessage(new ServerMessage("You just joined: " + getCurrentGameInstance().getInstanceName(), false));
            }

            printMessage(new ServerMessage("", false));
            printMessage(new ServerMessage("When ready please write ok: ", true));
            while (getReady()) // While the player is not ready, ask them to write ok to ready up.
            {
                String message = reader.readLine();
                if (message.equalsIgnoreCase("ok"))
                {
                    this.isReady = true;
                    currentGameInstance.gameBroadcast(new ServerMessage(ply.getName() + " are now ready", false));
                }
                else
                {
                    printMessage(new ServerMessage("Input not recognized, please write ok", true));
                }
            }
        } catch (IOException e) {
            if (this.player != null) {
                System.out.println(this.player.getName() + " has disconnected from the server!");
            }
            else {
                System.out.println("A player has disconnected from the server");
            }
            if (this.currentGameInstance != null) {
                this.currentGameInstance.removePlayer(this);
            }
        }
    }

    // Public getters and setters for the game instance.
    public void setCurrentGameInstance(Game instance) {
        this.currentGameInstance = instance;
    }
    public Game getCurrentGameInstance() {
        return this.currentGameInstance;
    }

    // Closes the connection to the client by sending them a stop signal.
    public void closeConnection()
    {
        printMessage(new ServerMessage("Connection has been closed!", false, true));
    }

    // Read the next line sent from the client.
    public String readResponse() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    // Public getters and setters for the player
    public synchronized Player getPlayer() {
        return player;
    }

    public synchronized boolean getReady()
    {
        return !isReady;
    }

    // Sends a message to the client.
    public void printMessage(Message message)
    {
        writer.println(message);
    }
}
