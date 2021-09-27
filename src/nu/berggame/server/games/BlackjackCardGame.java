package nu.berggame.server.games;

import nu.berggame.server.*;
import nu.berggame.shared.Dice;
import nu.berggame.shared.cards.Card;
import nu.berggame.shared.cards.CardDeck;
import nu.berggame.shared.messages.CardMessage;
import nu.berggame.shared.messages.DiceRollMessage;
import nu.berggame.shared.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackjackCardGame extends Game {

    private int targetScore;

    DummyClient dealer;

    public BlackjackCardGame(String instanceName, int maxPlayerCount, int targetScore) {
        super(instanceName, maxPlayerCount);
        this.targetScore = targetScore;
    }

    @Override
    public void runGame() {
        dealer = new DummyClient(new Player("Dealer"));
        dealer.getPlayer().setAlias("Dealer");
        getPlayers().add(0, dealer);

        while (!hasPlayerWon()) {
            for (int i = getPlayers().size()-1; i != -1; i--) {
                turn(getPlayers().get(i));
            }

            if (getPlayers().size() == 1) {
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
            for (User user : getPlayers()) {
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
    }

    @Override
    public void refresh() {
        this.closeGame();
    }

    private void turn(User player) {
        try {
            CardDeck deck = new CardDeck.Builder().pictureCardsAsTen(true).overrideAceValue(11).build();
            deck.shuffle();

            Player currentPlayer = player.getPlayer();

            gameBroadcast(new ServerMessage("", false));
            player.printMessage(new ServerMessage("It is your turn", false));
            gameBroadcast(new ServerMessage(currentPlayer.getName() + "'s turn", false), player);
            Thread.sleep(1000);

            if (currentPlayer.getAlias() == "Dealer") // If the player is the dealer
            {
                int dealerNumber = 0;

                Card[] cards = deck.drawCards(2);
                for (Card card : cards) {
                    dealerNumber += card.getCardValue();
                }

                while (dealerNumber < 17) // While the dealer's score is below 17 keep rolling the dice and adding the result to the dealer's score.
                {
                    dealerNumber += deck.drawCard().getCardValue();
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

            Card[] cards = deck.drawCards(2);
            for (Card card : cards) {
                currentScore += card.getCardValue();
            }

            gameBroadcast(new ServerMessage(currentPlayer.getName() + " draws " + currentScore, false));
            gameBroadcast(new CardMessage(cards));
            gameBroadcast(new ServerMessage("", false));

            currentPlayer.setData(currentScore); // Set the current player's data to their start score.
            if (playerIsBustOrBlackjack(player)) // Check if the player are bust or have blackjack.
            {
                return;
            }

            gameBroadcast(new ServerMessage(currentPlayer.getName() + "'s current score is: " + currentScore, false));
            Thread.sleep(500);

            boolean continueRound = true; // Keeps track of the player input, if the players wants to stand, stop the round.
            while (continueRound) // As long as their score is below 16 keep rolling with to dice.
            {
                Thread.sleep(500);
                if (!askForHitOrStand(player)) // Ask the player if they want to hit or stand, returns true if they want to hit.
                {
                    continueRound = false;
                    break;
                }

                gameBroadcast(new ServerMessage("", false));
                gameBroadcast(new ServerMessage(currentPlayer.getName() + " draws another card!", false), player);

                cards = deck.drawCards(1);
                int newScore = cards[0].getCardValue(); // The player wanted to hit, so they roll the dice.
                gameBroadcast(new ServerMessage(currentPlayer.getName() + " draws " + newScore, false));
                gameBroadcast(new CardMessage(cards));

                currentScore += newScore; // Add the roll to their score.
                gameBroadcast(new ServerMessage(currentPlayer.getName() + "'s current score is: " + currentScore, false));

                currentPlayer.setData(currentScore); // Save the current score in the player.

                if (playerIsBustOrBlackjack(player)) // Check if the player are bust or have blackjack.
                {
                    return;
                }
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
            gameBroadcast(new ServerMessage(currentPlayer.getName() + " has blackjack!", false));
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
                System.out.println(player.getPlayer().getName() + " has disconnected from: " + getInstanceName());
                gameBroadcast(new ServerMessage(player.getPlayer().getName() + " has disconnected from the server", false));
                removePlayer(player);
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

    private boolean hasPlayerWon() {
        for (User ply : getPlayers()) {
            if (ply.getPlayer().getScore() >= targetScore) {
                return true;
            }
        }
        return false;
    }

    private User[] getRoundWinners() // Get all winners of the current round.
    {
        ArrayList<User> roundWinners = new ArrayList<User>(); // Create a new arraylist to store winners that have won the round.

        for (User ply : getPlayers())
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
            for (User ply : getPlayers()) {
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

        for (User user : getPlayers()) {
            if (user.getPlayer().getScore() >= targetScore) {
                winners.add(user);
            }
        }

        return winners.toArray(new User[0]);
    }
}
