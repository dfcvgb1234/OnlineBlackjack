package nu.berggame.client;

import nu.berggame.shared.messages.CardMessage;
import nu.berggame.shared.messages.DiceRollMessage;
import nu.berggame.shared.messages.ServerMessage;

import java.util.*;

/**
 * Main program to run as a client, performs all logic regarding client input.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.3
 */

public class GameClient {
       
   public static void main(String[] args){
      Scanner input = new Scanner(System.in);
      
      Client client = new Client(); // Create a new instance of the Client class.

      String name;
      do {
         System.out.print("What is your name: ");
         name = input.nextLine();
      }
      while (containsIllegalCharacters(name)); // Make sure the player name does not contain illegal characters.

      System.out.println();
      System.out.print("What is the server IP: ");
      String ip = input.nextLine();

      if (!client.startConnection(ip, 6060, name)) {
         return;
      }

      label:
      while (true) {
         String clientMessage = client.readMessage();
         String messageType = clientMessage.split(";")[0]; // Get the message type from the server.
                                                                 // This is used to create an instance of the correct message class.
                                                                 // The message classes are used to send specific messages to the clients, with specific actions.
         switch (messageType) {
            case "msg":
               if (!printMessage(new ServerMessage(clientMessage), client, input)) { // The ServerMessage class is a simple message class, this sends a plain text message.
                  break label;
               }
               break;
            case "drm":
               renderDice(new DiceRollMessage(clientMessage)); // The DiceRollMessage class tells the client that it should render dice in the received format.

               break;
            case "cm":
               renderCards(new CardMessage(clientMessage)); // The CardMessage class tells the client that it should render cards in the received format.

               break;
         }
      }

      client.stopConnection();
   }

   // Checks if a string contains a list of illegal characters.
   private static boolean containsIllegalCharacters(String name) {
      String nonAllowedChars = ";:";
      for (int i = 0; i < nonAllowedChars.length(); i++) {
         if (name.contains("" + nonAllowedChars.charAt(i))) {
            return true;
         }
      }
      return false;
   }

   // Calls the GameRenderer class to render card.
   private static void renderCards(CardMessage cm) {
      GameRenderer.renderCards(cm.getCards());
   }

   // Class the GameRenderer class to render dice.
   private static void renderDice(DiceRollMessage drm) {
      GameRenderer.renderDice(drm.getDice());
   }

   // Processes a ServerMessage object, to print the message received from the server.
   private static boolean printMessage(ServerMessage msg, Client client, Scanner input) {
      if (msg.isExpectInput()) { // If the message contained a flag specifying input is needed, read input from player.
            System.out.print(msg.getMessage());
            String response = input.next();
            client.sendMessage(response);
         }
         else {
            System.out.println(msg.getMessage()); // If no input is needed just print the message.
         }

      // If the message contained a stop signal return false.
      return !msg.isStopSignal();
   }
}