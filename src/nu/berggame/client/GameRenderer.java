package nu.berggame.client;

import nu.berggame.shared.Dice;
import nu.berggame.shared.cards.Card;

/**
 * Renders content for the game when received from the server.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.9
 */

public class GameRenderer {
   
   public static final String DICE_TOP = "╔═══════╗"; // How the top border of the dice is shown.
   public static final String DICE_BOTTOM = "╚═══════╝"; // How the bottom border of the dice is shown.
   public static final char DICE_BORDER_CHAR = '║'; // How the border of the dice is shown.
   public static final char DICE_DOT_CHAR = '•'; // How the eyes of the dice is shown.

   /*
      ╭──────────╮
      │ 3        │
      │ ♣        │
      │          │
      │        ♣ │
      │        3 │
      ╰──────────╯
   */

   /*
      ╔═══════╗
      ║ •   • ║
      ║ •   • ║
      ║ •   • ║
      ╚═══════╝
   */

   // Prints the card layer by layer depending on how many cards are needed.
   public static void renderCards(Card[] cards) {

      for (Card card : cards) {
         System.out.print("╭──────────╮ ");
      }
      System.out.println();
      for (Card card : cards) {
         System.out.printf("│ %-9s│ ", card.getCardValue());
      }
      System.out.println();
      for (Card card : cards) {
         System.out.printf("│ %-9s│ ", getCardSymbol(card.getCardSymbol()));
      }
      System.out.println();
      for (Card card : cards) {
         System.out.print("│          │ ");
      }
      System.out.println();
      for (Card card : cards) {
         System.out.printf("│%9s │ ", getCardSymbol(card.getCardSymbol()));
      }
      System.out.println();
      for (Card card : cards) {
         System.out.printf("│%9s │ ", card.getCardValue());
      }
      System.out.println();
      for (Card card : cards) {
         System.out.print("╰──────────╯ ");
      }
      System.out.println();
   }

   // Gets the card symbol based on the enum value the card has been assigned.
   private static String getCardSymbol(Card.CardSymbol symbol) {
      String resultSymbol = "";
      switch (symbol) {
         case Club:
            resultSymbol = "♣";
            break;

         case Heart:
            resultSymbol = "♥";
            break;

         case Spade:
            resultSymbol = "♤";
            break;

         case Diamond:
            resultSymbol = "♦";
            break;

         case Joker:
            resultSymbol = "J";
            break;
      }

      return resultSymbol;
   }

   // Renders dice layer by layer depending on how many dice are needed.
   public static void renderDice(Dice[] dice) {
      int diceCount = dice.length;

      for (Dice die : dice) { // Foreach dice print the top layer.
         printTop();
      }
      System.out.println();
      // Foreach dice print the first layer.
      for (Dice die : dice) {
         printFirstLayer(die.getCurrentFace());
      }
      System.out.println();
      // Foreach dice print the second layer.
      for (Dice die : dice) {
         printSecondLayer(die.getCurrentFace());
      }
      System.out.println();
      // Foreach dice print the third layer.
      for (Dice die : dice) {
         printThirdLayer(die.getCurrentFace());
      }
      System.out.println();
      for (Dice die : dice) { // Foreach dice print the bottom layer
         printBottom();
      }
      System.out.println();

   }
   
   private static void printTop() { // Print the defined top.

      System.out.print(DICE_TOP);
      System.out.print(" "); // Make space for the next dice on the same line.
   }
   
   private static void printFirstLayer(int diceResult) {  // Prints the first layer of the dice, with the defined chars.
      switch(diceResult) { // Write the first layer of dice based on the dice value.
         case 1:
            System.out.print(DICE_BORDER_CHAR + "       " + DICE_BORDER_CHAR);
            break;
           
         case 2:
            System.out.print(DICE_BORDER_CHAR + " " + DICE_DOT_CHAR + "     " + DICE_BORDER_CHAR);
            break;
      
         case 3:
            System.out.print(DICE_BORDER_CHAR + " " + DICE_DOT_CHAR + "     " + DICE_BORDER_CHAR);
            break;
      
         case 4:
            System.out.print(DICE_BORDER_CHAR + " " + DICE_DOT_CHAR + "   " + DICE_DOT_CHAR + " " + DICE_BORDER_CHAR);
            break;
      
         case 5:
            System.out.print(DICE_BORDER_CHAR + " " + DICE_DOT_CHAR + "   " + DICE_DOT_CHAR + " " + DICE_BORDER_CHAR);
            break;
      
         case 6:
            System.out.print(DICE_BORDER_CHAR + " " + DICE_DOT_CHAR + "   " + DICE_DOT_CHAR + " " + DICE_BORDER_CHAR);
            break;
            
         default:
            System.out.print(DICE_BORDER_CHAR + "       " + DICE_BORDER_CHAR); // If the dice has more than one side, print this specially.
            break;
      
      }
      System.out.print(" "); // Make space for more dice on the same line.
   }

   
   private static void printSecondLayer(int diceResult) { // Prints the second layer using the same concept as above.
      switch(diceResult) {
         case 1:
            System.out.print(DICE_BORDER_CHAR + "   " + DICE_DOT_CHAR + "   " + DICE_BORDER_CHAR);
            break;
           
         case 2:
            System.out.print(DICE_BORDER_CHAR + "       " + DICE_BORDER_CHAR);
            break;
      
         case 3:
            System.out.print(DICE_BORDER_CHAR + "   " + DICE_DOT_CHAR + "   " + DICE_BORDER_CHAR);
            break;
      
         case 4:
            System.out.print(DICE_BORDER_CHAR + "       " + DICE_BORDER_CHAR);
            break;
      
         case 5:
            System.out.print(DICE_BORDER_CHAR + "   " + DICE_DOT_CHAR + "   " + DICE_BORDER_CHAR);
            break;
      
         case 6:
            System.out.print(DICE_BORDER_CHAR + " " + DICE_DOT_CHAR + "   " + DICE_DOT_CHAR + " " + DICE_BORDER_CHAR);
            break;
            
         default:
            System.out.print(DICE_BORDER_CHAR + "   " + diceResult + "   " + DICE_BORDER_CHAR);
            break;
      }
      System.out.print(" ");
   }
   
   private static void printThirdLayer(int diceResult) { // Prints the third layer using the same concept as above.
      switch(diceResult) {
         case 1:
            System.out.print(DICE_BORDER_CHAR + "       " + DICE_BORDER_CHAR);
            break;
           
         case 2:
            System.out.print(DICE_BORDER_CHAR + "     " + DICE_DOT_CHAR + " " + DICE_BORDER_CHAR);
            break;
      
         case 3:
            System.out.print(DICE_BORDER_CHAR + "     " + DICE_DOT_CHAR + " " + DICE_BORDER_CHAR);
            break;
      
         case 4:
            System.out.print(DICE_BORDER_CHAR + " " + DICE_DOT_CHAR + "   " + DICE_DOT_CHAR + " " + DICE_BORDER_CHAR);
            break;
      
         case 5:
            System.out.print(DICE_BORDER_CHAR + " " + DICE_DOT_CHAR + "   " + DICE_DOT_CHAR + " " + DICE_BORDER_CHAR);
            break;
      
         case 6:
            System.out.print(DICE_BORDER_CHAR + " " + DICE_DOT_CHAR + "   " + DICE_DOT_CHAR + " " + DICE_BORDER_CHAR);
            break;
            
         default:
            System.out.print(DICE_BORDER_CHAR + "       " + DICE_BORDER_CHAR);
            break;
      }
      System.out.print(" ");
   }
   
   private static void printBottom() { // Prints the bottom layer of the dice.
      System.out.print(DICE_BOTTOM);
      System.out.print(" ");
   }

}