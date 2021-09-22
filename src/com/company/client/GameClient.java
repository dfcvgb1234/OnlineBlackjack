package com.company.client;

import com.company.shared.messages.DiceRollMessage;
import com.company.shared.messages.ServerMessage;

import java.util.*;

public class GameClient {
       
   public static void main(String[] args){
      Scanner input = new Scanner(System.in);
      
      Client client = new Client();

      String name = ";";
      while (containsNonAllowedCharacters(name)) {
         System.out.print("What is your name: ");
         name = input.nextLine();
      }

      System.out.println();
      System.out.print("What is the server IP: ");
      String ip = input.nextLine();

      if (!client.startConnection(ip, 6060)) {
         return;
      }
      client.sendMessage(name);
      while (true) {
         String clientMessage = client.readMessage();
         String messageType = clientMessage.split(";")[0];
         if (messageType.equals("msg")) {
            if (!printMessage(new ServerMessage(clientMessage), client, input)) {
               break;
            }
         }
         else {
            renderDice(new DiceRollMessage(clientMessage));
         }
         
      }
      
      client.stopConnection();
   }

   private static boolean containsNonAllowedCharacters(String name) {
      String nonAllowedChars = ";:";
      for (int i = 0; i < nonAllowedChars.length(); i++) {
         if (name.contains("" + nonAllowedChars.charAt(i))) {
            return true;
         }
      }
      return false;
   }

   private static void renderDice(DiceRollMessage drm) {
      GameRenderer.renderDice(drm.getDice());
   }
   
   private static boolean printMessage(ServerMessage msg, Client client, Scanner input) {
      if (msg.isExpectInput()) {
            System.out.print(msg.getMessage());
            String response = input.next();
            client.sendMessage(response);
         }
         else {
            System.out.println(msg.getMessage());
         }
         
         if (msg.isStopSignal()) {
            return false;
         }
         else {
            return true;
         }
   }
}