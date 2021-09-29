package nu.berggame.shared.messages;

import nu.berggame.shared.cards.Card;

/**
 * Represents a message that tells the client to draw a card.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.5
 */

public class CardMessage implements Message{
    Card[] cards;

    // Constructor which takes an array of cards to send to the client.
    public CardMessage(Card[] cards) {
        this.cards = cards;
    }

    // Constructor which takes a raw message from the server and parses the string into useful objects.
    public CardMessage(String rawMessage) {
        String[] messageContent = rawMessage.split(";");
        this.cards = new Card[messageContent.length-1];
        for (int i = 1; i < messageContent.length; i++) {
            this.cards[i-1] = new Card(parseSymbol(messageContent[i].split(":")[1]), Integer.parseInt(messageContent[i].split(":")[0]));
        }
    }

    // Getter for the card array.
    public Card[] getCards() {
        return cards;
    }

    // Parses the string value of the CardSymbol enum.
    private Card.CardSymbol parseSymbol(String symbol) {
        Card.CardSymbol resultSymbol = null;
        switch (symbol) {
            case "Club":
                resultSymbol = Card.CardSymbol.Club;
                break;

            case "Spade":
                resultSymbol = Card.CardSymbol.Spade;
                break;

            case "Heart":
                resultSymbol = Card.CardSymbol.Heart;
                break;

            case "Diamond":
                resultSymbol = Card.CardSymbol.Diamond;
                break;

            case "Joker":
                resultSymbol = Card.CardSymbol.Joker;
                break;
        }

        return resultSymbol;
    }

    // Turns this object into a String representation that can be read by itself when it reaches the client.
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Card card : cards) {
            result.append(";").append(card.getCardValue()).append(":").append(card.getCardSymbol().toString()); // Creates a separated list, which can be split up when it should be parsed.
        }

        return "cm" + result;
    }
}
