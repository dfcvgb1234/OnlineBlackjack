package nu.berggame.shared.messages;

import nu.berggame.shared.cards.Card;

public class CardMessage implements Message{
    Card[] cards;

    public CardMessage(Card[] cards) {
        this.cards = cards;
    }

    public CardMessage(String rawMessage) {
        String[] messageContent = rawMessage.split(";");
        this.cards = new Card[messageContent.length-1];
        for (int i = 1; i < messageContent.length; i++) {
            this.cards[i-1] = new Card(parseSymbol(messageContent[i].split(":")[1]), Integer.parseInt(messageContent[i].split(":")[0]));
        }
    }

    public Card[] getCards() {
        return cards;
    }

    private Card.CardSymbol parseSymbol(String symbol) {
        Card.CardSymbol resultSymbol = Card.CardSymbol.Club;
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

    @Override
    public String toString() {
        String result = "";
        for (Card card : cards) {
            result += ";" + card.getCardValue() + ":" + card.getCardSymbol().toString();
        }

        return "cm" + result;
    }
}
