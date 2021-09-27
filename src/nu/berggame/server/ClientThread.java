package nu.berggame.server;

import nu.berggame.shared.messages.Message;
import nu.berggame.shared.messages.ServerMessage;

import java.io.*;
import java.net.*;

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

        OutputStream output = null;
        try {
            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        super.run();
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));

            String playerName = reader.readLine();
            System.out.println("A player joined: " + playerName);
            Player ply = new Player(playerName);
            this.player = ply;

            String selectedGame = "";
            boolean gameSelected = false;
            while (gameSelected == false) {
                printMessage(new ServerMessage("Game list:", false));
                String[] gameList = server.getGameList();
                for (int i = 0; i < gameList.length; i++) {
                    printMessage(new ServerMessage((i+1) + ". " + gameList[i], false));
                }
                printMessage(new ServerMessage("", false));
                printMessage(new ServerMessage("Please select a game: ", true));
                String message = reader.readLine();
                try {
                    int selectedGameIndex = Integer.parseInt(message)-1;
                    if (selectedGameIndex < 0 || selectedGameIndex > gameList.length-1) {
                        printMessage(new ServerMessage("Please enter a numerical value between 1 and " + gameList.length, false));
                        printMessage(new ServerMessage("", false));
                        gameSelected = false;
                    }
                    else{
                        selectedGame = gameList[selectedGameIndex];
                        gameSelected = true;
                        printMessage(new ServerMessage("", false));
                        printMessage(new ServerMessage("looking for game...", false));
                    }
                }
                catch (Exception ex) {
                    printMessage(new ServerMessage("Please enter a numerical value between 1 and " + gameList.length, false));
                    printMessage(new ServerMessage("", false));
                    gameSelected = false;
                }
            }

            if (!server.findNextAvailableServer(this, selectedGame)) {
                printMessage(new ServerMessage("No open servers found!", false));
                this.closeConnection();
                return;
            }
            else {
                System.out.println(this.player.getName() + " just joined: " + getCurrentGameInstance().getInstanceName());
                printMessage(new ServerMessage("You just joined: " + getCurrentGameInstance().getInstanceName(), false));
            }

            printMessage(new ServerMessage("", false));
            printMessage(new ServerMessage("When ready please write ok: ", true));
            while (!getReady())
            {
                String message = reader.readLine();
                if (message.equalsIgnoreCase("ok"))
                {
                    this.isReady = true;
                    server.broadcast(new ServerMessage(ply.getName() + " are now ready", false));
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
            this.currentGameInstance.removePlayer(this);
        }
    }

    public void setCurrentGameInstance(Game instance) {
        this.currentGameInstance = instance;
    }
    public Game getCurrentGameInstance() {
        return this.currentGameInstance;
    }

    public void closeConnection()
    {
        printMessage(new ServerMessage("Connection has been closed!", false, true));
    }

    public String readResponse() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public synchronized Player getPlayer() {
        return player;
    }

    public synchronized boolean getReady()
    {
        return isReady;
    }

    public void printMessage(Message message)
    {
        writer.println(message);
    }
}
