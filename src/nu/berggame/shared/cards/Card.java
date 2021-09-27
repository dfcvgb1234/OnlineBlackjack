package nu.berggame.shared.cards;

import nu.berggame.shared.Component;

public class Card implements Component {

    private CardSymbol cardSymbol;
    private int cardValue;

    private CardDeck deck;

    private boolean hasSpecialName;
    private CardSpecialValue specialValue;

    public Card (CardSymbol symbol, int cardValue) {
        this.cardSymbol = symbol;
        this.cardValue = cardValue;
    }

    public Card() {

    }

    public void setCardDeck (CardDeck deck) {
        this.deck = deck;
    }

    public static CardSpecialValue getSpecialFromValue(int value) {
        CardSpecialValue specialValue = null;

        switch (value) {
            case 1:
            case 14:
                specialValue = CardSpecialValue.Ace;
                break;

            case 11:
                specialValue = CardSpecialValue.Jack;
                break;

            case 12:
                specialValue = CardSpecialValue.Queen;
                break;

            case 13:
                specialValue = CardSpecialValue.King;
                break;
        }

        return specialValue;
    }

    public static int getValueFromSpecial(CardSpecialValue specialValue, boolean highAce) {
        int cardValue = 0;

        switch (specialValue) {
            case Ace:
                if (highAce) {
                    cardValue = 14;
                } else {
                    cardValue =  1;
                }
                break;

            case Jack:
                cardValue = 11;
                break;

            case Queen:
                cardValue = 12;
                break;

            case King:
                cardValue = 13;
                break;
        }
        return cardValue;
    }

    public CardSymbol getCardSymbol() {
        return cardSymbol;
    }

    public int getCardValue() {
        return cardValue;
    }

    public CardDeck getDeck() {
        return deck;
    }

    public enum CardSymbol {
        Club,
        Diamond,
        Heart,
        Spade,
        Joker
    }

    public enum CardSpecialValue {
        Ace,
        Jack,
        Queen,
        King
    }
}


