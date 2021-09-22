package com.company.client;

import java.io.*;
import java.net.*;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

   public boolean startConnection(String ip, int port) {
      try
      {
         clientSocket = new Socket(ip, port);
         out = new PrintWriter(clientSocket.getOutputStream(), true);
         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
      try
      {
         return in.readLine();
      }
      catch (Exception ex) {
         ex.printStackTrace();
         System.out.println("Connection lost!");
         stopConnection();
         System.exit(0);
         return "";
      }
   }

   public void stopConnection() {
      try
      {
         in.close();
         out.close();
         clientSocket.close();
      }
      catch(Exception ex) {
         ex.printStackTrace();
      }
   }
}