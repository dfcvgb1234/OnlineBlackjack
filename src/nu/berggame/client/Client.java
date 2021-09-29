package nu.berggame.client;

import java.io.*;
import java.net.*;

/**
 * Client class used to connect and disconnect from a server, and send and receive messages from a server.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 1.2
 */

public class Client {
   private Socket clientSocket;
   private PrintWriter out;
   private BufferedReader in;

   public boolean startConnection(String ip, int port, String playerName) {
      try {
         clientSocket = new Socket(ip, port); // Client socket the user uses to connect to the server socket.
         out = new PrintWriter(clientSocket.getOutputStream(), true); // Writes to the sockets output stream, this sends messages to the server.
         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Reads all incoming messages from the server.

         sendMessage(playerName); // Start of by sending player name to the server.

         return true;
      }
      catch(Exception ex) {
         System.out.println("Could not connect to the server: " + ex.getMessage());
         return false;
      }
   }

   public void sendMessage(String msg) {
        out.println(msg);
   }
    
   public String readMessage() {
      try {
         return in.readLine();
      }
      catch (Exception ex) {
         System.out.println("Connection lost. Reason: " + ex.getMessage()); // It is usually only noticed that a disconnect occurred when reading from the server.
         stopConnection();
         System.exit(0);
         return "";
      }
   }

   public void stopConnection() {
      try {
         in.close();
         out.close();
         clientSocket.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
      }
   }
}