package com.company.server;

import com.company.shared.Dice;
import com.company.shared.messages.DiceRollMessage;
import com.company.shared.messages.Message;
import com.company.shared.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackjackGame implements Game, Runnable{

    private final int timeoutSeconds = 5;

    private String instanceName = "";
    private int targetScore;

    DummyClient dealer;

    private List<User> players;
    private int maxPlayerCount;

    private boolean alive;

    public BlackjackGame(String instanceName, int maxPlayerCount, int targetScore)
    {
        players = new ArrayList<>();
        this.instanceName = instanceName;
        this.maxPlayerCount = maxPlayerCount;
        this.targetScore = targetScore;
        this.alive = false;
    }

    @Override
    public void run() {

        alive = true;
        int iteration = 0;
        while(!allPlayersReady()) {
            try {
                Thread.sleep(1000);

                if (iteration == timeoutSeconds) {

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

        gameBroadcast(new ServerMessage("All players ready, starting game!", false));

        int currentPlayerIndex = 0;

        dealer = new DummyClient(new Player("Dealer"));
        dealer.getPlayer().setAlias("Dealer");
        players.add(0, dealer);

        while (!hasPlayerWon()) {
            for (int i = players.size()-1; i != -1; i--) {
                turn(players.get(i));
            }

            if (players.size() == 1) {
                break;
            }

            User[] roundWinners = getRoundWinners(); // After all players have had their turn, get the winners.
            gameBroadcast(new ServerMessage("", false));
            if (roundWinners == null || roundWinners.length == 0)
            {
                gameBroadcast(new ServerMessage("No winners this round!", false));
            }
            else
            {
                for (User roundWinner : roundWinners) // Loop through all winners.
                {
                    roundWinner.printMessage(new ServerMessage("You have won this round!", false));
                    gameBroadcast(new ServerMessage(roundWinner.getPlayer().getName() + " has won this round!", false), roundWinner);
                    if (roundWinner.getPlayer().getData() == 21)
                    {
                        roundWinner.getPlayer().addScore(2); // If they scored 21 add two points to their score.
                    }
                    else
                    {
                        roundWinner.getPlayer().addScore(1); // Else add only one point to their score.
                    }
                }
            }
            gameBroadcast(new ServerMessage("", false));
            gameBroadcast(new ServerMessage("Current Score: ", false));
            gameBroadcast(new ServerMessage("------------------------", false));
            for (User user : players) {
                gameBroadcast(new ServerMessage(user.getPlayer().getName() + " - " + user.getPlayer().getScore(), false));
                gameBroadcast(new ServerMessage("------------------------", false));
            }
            gameBroadcast(new ServerMessage("", false));
        }

        User[] gameWinners = getAllGameWinners();
        for (User winner : gameWinners) {
            winner.printMessage(new ServerMessage("YOU HAVE WON THE GAME!!", false));
            gameBroadcast(new ServerMessage(winner.getPlayer().getName() + " has won the game!", false), winner);
        }

        closeGame();
        alive = false;
    }

    public synchronized boolean isAlive() {
        return this.alive;
    }

    private void turn(User player) {
        try {
            Dice[] dice = new Dice[2];
            dice[0] = new Dice(6);
            dice[1] = new Dice(6);

            Player currentPlayer = player.getPlayer();

            gameBroadcast(new ServerMessage("", false));
            player.printMessage(new ServerMessage("It is your turn", false));
            gameBroadcast(new ServerMessage(currentPlayer.getName() + "'s turn", false), player);
            Thread.sleep(1000);

            if (currentPlayer.getAlias() == "Dealer") // If the player is the dealer
            {
                Random rand = new Random();
                int dealerNumber = 0;

                while (dealerNumber < 17) // While the dealer's score is below 17 keep rolling the dice and adding the result to the dealer's score.
                {
                    dealerNumber += rand.nextInt(7) + 1;
                }
                gameBroadcast(new ServerMessage("Dealer ends on: " + dealerNumber, false));
                Thread.sleep(600);

                if (dealerNumber == 21) // If the dealer scored 21, make sure they win the round.
                {
                    gameBroadcast(new ServerMessage("Dealer got blackjack!", false));
                    currentPlayer.setData(99);
                } else if (dealerNumber > 21) // If the dealer scored greater than 21, make them bust.
                {
                    gameBroadcast(new ServerMessage("Dealer was bust!", false));
                    currentPlayer.setData(0);
                } else {
                    currentPlayer.setData(dealerNumber); // Else set the dealer's score for the round to the rolled amount.
                }

                return; // End the turn.
            }
            gameBroadcast(new ServerMessage("", false));

            int currentScore = 0;

            currentScore = rollAllDice(dice);
            gameBroadcast(new ServerMessage(currentPlayer.getName() + " rolls " + currentScore, false));
            gameBroadcast(new DiceRollMessage(dice));
            gameBroadcast(new ServerMessage("", false));

            gameBroadcast(new ServerMessage(currentPlayer.getName() + "'s current score is: " + currentScore, false));
            currentPlayer.setData(currentScore); // Set the current player's data to their start score.
            Thread.sleep(500);

            boolean continueRound = true; // Keeps track of the player input, if the players wants to stand, stop the round.
            while (continueRound && currentScore < 16) // As long as their score is below 16 keep rolling with to dice.
            {
                Thread.sleep(500);
                if (!askForHitOrStand(player)) // Ask the player if they want to hit or stand, returns true if they want to hit.
                {
                    continueRound = false;
                    break;
                }

                gameBroadcast(new ServerMessage("", false));
                gameBroadcast(new ServerMessage(currentPlayer.getName() + " rolls the dice!", false));

                int newScore = rollAllDice(dice); // The player wanted to hit, so they roll the dice.
                gameBroadcast(new ServerMessage(currentPlayer.getName() + " rolls " + newScore, false));
                gameBroadcast(new DiceRollMessage(dice));

                currentScore += newScore; // Add the roll to their score.
                gameBroadcast(new ServerMessage(currentPlayer.getName() + "'s current score is: " + currentScore, false));

                currentPlayer.setData(currentScore); // Save the current score in the player.
            }

            if (playerIsBustOrBlackjack(player)) // When the player's score is greater that 16, check if they are bust or have blackjack.
            {
                return;
            }

            while (continueRound && currentScore < 22) // As long as the player's score is less than 22 keep playing with one dice.
            {
                Thread.sleep(500);
                if (!askForHitOrStand(player)) // Ask the player if the want to hit or stand.
                {
                    continueRound = false;
                    break;
                }

                gameBroadcast(new ServerMessage("", false));
                gameBroadcast(new ServerMessage(currentPlayer.getName() + " rolls the dice!", false));

                Dice[] oneDice = new Dice[1];
                oneDice[0] = new Dice(6);
                int newScore = rollAllDice(oneDice); // Roll only one dice
                gameBroadcast(new ServerMessage(currentPlayer.getName() + " rolls " + newScore, false));
                gameBroadcast(new DiceRollMessage(oneDice));

                currentScore += newScore; // Add the roll to the current score.
                gameBroadcast(new ServerMessage(currentPlayer.getName() + "'s current score is: " + currentScore, false));

                currentPlayer.setData(currentScore); // Save the current score in the player.

                if (currentScore >= 21) // If the score is greater than or equal to 21 exit the loop.
                {
                    break;
                }
            }

            if (playerIsBustOrBlackjack(player)) // Check if the player is bust or have blackjack.
            {
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean playerIsBustOrBlackjack(User player)
    {
        Player currentPlayer = player.getPlayer();
        if (currentPlayer.getData() > 21) // When their score is greater than 21 the player are bust
        {
            gameBroadcast(new ServerMessage("", false));
            gameBroadcast(new ServerMessage(currentPlayer.getName() + " went bust!", false));
            currentPlayer.setData(0); // Reset the saved score of the player to 0, so when comparisons are made the player loses.
            return true;
        }
        else if (currentPlayer.getData() == 21) // When their score is 21 the player have rolled a blackjack
        {
            gameBroadcast(new ServerMessage("", false));
            gameBroadcast(new ServerMessage(currentPlayer.getName() + " rolled blackjack!", false));
            return true;
        }

        return false;
    }

    private boolean askForHitOrStand(User player) {
        gameBroadcast(new ServerMessage("", false));
        gameBroadcast(new ServerMessage("Waiting for " + player.getPlayer().getName() + " to make a move", false), player);

        String response = "";
        while (response.equals("")) {
            player.printMessage(new ServerMessage("Do you wish to hit or stand? ", true));
            response = player.readResponse();

            if (response == null) {
                System.out.println(player.getPlayer().getName() + " has disconnected from: " + this.instanceName);
                gameBroadcast(new ServerMessage(player.getPlayer().getName() + " has disconnected from the server", false));
                players.remove(player);
                return false;
            }

            if (response.equalsIgnoreCase("h") || response.equalsIgnoreCase("hit")) {
                return true;
            }
            if (response.equalsIgnoreCase("s") || response.equalsIgnoreCase("stand")) {
                return false;
            }

            response = "";
        }

        return false;
    }

    private int rollAllDice(Dice[] dice) {
        int result = 0;
        for (Dice die : dice) {
            result += die.roll();
        }
        return result;
    }

    private boolean hasPlayerWon() {
        for (User ply : players) {
            if (ply.getPlayer().getScore() >= targetScore) {
                return true;
            }
        }
        return false;
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

    private void closeGame() {
        for (User ply : players) {
            ply.closeConnection();
        }

        players.clear();
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

    public synchronized boolean addPlayer(User player) {
        if (this.isAlive() == true) {
            return false;
        }
        if (players.size() == this.maxPlayerCount)
        {
            return false;
        }
        else
        {
            players.add(player);
            return true;
        }
    }

    public synchronized void removePlayer(User player) {
        players.remove(player);
    }

    private User[] getRoundWinners() // Get all winners of the current round.
    {
        ArrayList<User> roundWinners = new ArrayList<User>(); // Create a new arraylist to store winners that have won the round.

        for (User ply : players)
        {
            if (!ply.getPlayer().getAlias().equalsIgnoreCase("dealer")) // If the player has a greater score than the dealer save them to the winner array.
            {
                if (ply.getPlayer().getData() > dealer.getPlayer().getData())
                {
                    roundWinners.add(ply);
                }
            }
        }

        if (roundWinners.size() == 0)
        {
            for (User ply : players) {
                if (!ply.getPlayer().getAlias().equalsIgnoreCase("dealer")) {
                    if (ply.getPlayer().getData() == dealer.getPlayer().getData()) {
                        dealer.getPlayer().setData(0);
                    }
                }
            }

            if (dealer.getPlayer().getData() != 0)
            {
                if (dealer.getPlayer().getData() == 99) {
                    dealer.getPlayer().setData(21);
                }
                roundWinners.add(dealer); // If no winner was found between the player, assume the dealer has won.
            }
        }
        return roundWinners.toArray(new User[0]); // Turn the player list to a player array.

    }

    private User[] getAllGameWinners() {
        List<User> winners = new ArrayList<User>();

        for (User user : players) {
            if (user.getPlayer().getScore() >= targetScore) {
                winners.add(user);
            }
        }

        return winners.toArray(new User[0]);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public String getInstanceName() {
        return this.instanceName;
    }

}
