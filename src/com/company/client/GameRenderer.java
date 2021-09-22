package com.company.client;

import com.company.shared.Dice;

public class GameRenderer {
   
   public static final String DICE_TOP = "/-------\\"; // How the top border of the dice is shown.
   public static final String DICE_BOTTOM = "\\-------/"; // How the bottom border of the dice is shown.
   public static final char DICE_BORDER_CHAR = '|'; // How the border of the dice is shown.
   public static final char DICE_DOT_CHAR = 'o'; // How the eyes of the dice is shown.
   
   public static void renderDice(Dice[] dice) {
      int diceCount = dice.length;

      for (int i = 0; i < diceCount; i++) // Foreach dice print the top layer.
      {
         printTop();
      }
      System.out.println();
      for (int i = 0; i < diceCount; i++) // Foreach dice print the first layer.
      {
         printFirstLayer(dice[i].getCurrentFace());
      }
      System.out.println();
      for (int i = 0; i < diceCount; i++) // Foreach dice print the second layer.
      {
         printSecondLayer(dice[i].getCurrentFace());
      }
      System.out.println();
      for (int i = 0; i < diceCount; i++) // Foreach dice print the third layer.
      {
         printThirdLayer(dice[i].getCurrentFace());
      }
      System.out.println();
      for (int i = 0; i < diceCount; i++) // Foreach dice print the bottom layer
      {
         printBottom();
      }
      System.out.println();

   }
   
   private static void printTop() // Print the defined top.
   {
      System.out.print(DICE_TOP);
      System.out.print(" "); // Make space for the next dice on the same line.
   }
   
   private static void printFirstLayer(int diceResult) // Prints the first layer of the dice, with the defined chars.
   {   
      switch(diceResult) // Write the first layer of dice based on the dicevalue.
      {
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

   
   private static void printSecondLayer(int diceResult) // Prints the second layer using the same concept as above.
   {
      switch(diceResult)
      {
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
   
   private static void printThirdLayer(int diceResult) // Prints the third layer using the same concept as above.
   {   
      switch(diceResult)
      {
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
   
   private static void printBottom() // Prints the bottom layer of the dice.
   {
      System.out.print(DICE_BOTTOM);
      System.out.print(" ");
   }

}