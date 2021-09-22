package com.company.server;

import com.company.shared.messages.Message;
import com.company.shared.messages.ServerMessage;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread implements User {

    private boolean isReady;
    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;
    private BlackjackServer server;
    private Player player;

    private Game currentGameInstance;

    public ClientThread(Socket socket, BlackjackServer server) {
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
            System.out.println("A player joined: " + playerName + " | Location: " + this.getCurrentGameInstance().getInstanceName());
            Player ply = new Player(playerName);
            this.getCurrentGameInstance().gameBroadcast(new ServerMessage(ply.getName() + " has joined the game!", false), this);
            printMessage(new ServerMessage("You have joined: " + this.getCurrentGameInstance().getInstanceName(), false));
            this.player = ply;

            printMessage(new ServerMessage("write OK when ready to play: ", true));

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
