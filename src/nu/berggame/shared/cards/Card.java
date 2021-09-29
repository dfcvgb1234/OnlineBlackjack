package nu.berggame.shared.cards;

import nu.berggame.shared.Component;

/**
 * Card class which represents a single playing card.
 *
 * @implNote This system is not yet complete, it is missing a ton of features.
 *           A big feature implemented next would be context representation.
 *           Where the card can change value and representation based on an outside context.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.3
 */

public class Card implements Component {

    private CardSymbol cardSymbol; // Symbol of a card, represented by an enum
    private int cardValue; // Constant card value, expected to change to be more flexible.

    private CardDeck deck; // Reference to the current card deck it is a member of.

    // Only constructor available to a card, since it must always have a symbol and a value.
    public Card (CardSymbol symbol, int cardValue) {
        this.cardSymbol = symbol;
        this.cardValue = cardValue;
    }

    public void setCardDeck (CardDeck deck) {
        this.deck = deck;
    }

    public CardDeck getCardDeck() {
        return deck;
    }

    public CardSymbol getCardSymbol() {
        return cardSymbol;
    }

    public int getCardValue() {
        return cardValue;
    }

    // Enum with the different card symbols
    public enum CardSymbol {
        Club,
        Diamond,
        Heart,
        Spade,
        Joker
    }
}


