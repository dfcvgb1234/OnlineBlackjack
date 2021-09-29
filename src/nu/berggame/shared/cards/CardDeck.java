package nu.berggame.shared.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * CardDeck class is a container for playing cards.
 *
 * @implNote This system is not yet complete, it is missing a ton of features.
 *           A big feature implemented next would be more builder control to improve implementation.
 *
 * @author  Sebastian Berg Rasmussen
 * @version 0.4
 */

public class CardDeck {

    // Static builder class for the card deck.
    // This is used to have an easy implementation of a card deck.
    public static class Builder {

        private int knightValueOverride = 11;
        private int queenValueOverride = 12;
        private int kingValueOverride = 13;
        private int aceValueOverride = 14;

        public Builder pictureCardsAsTen(boolean enabled) { // This changes all picture cards to have a value of ten.
            this.kingValueOverride = 10;
            this.queenValueOverride = 10;
            this.knightValueOverride = 10;

            return this;
        }

        public Builder overrideKnightValue(int newValue) { // Override the value of the knight.
            this.knightValueOverride = newValue;

            return this;
        }

        public Builder overrideQueenValue(int newValue) { // Override the value of the queen.
            this.queenValueOverride = newValue;

            return this;
        }

        public Builder overrideKingValue(int newValue) { // Override the value of the king.
            this.kingValueOverride = newValue;

            return this;
        }

        public Builder overrideAceValue(int newValue) { // Override the value of an ace.
            this.aceValueOverride = newValue;

            return this;
        }

        public CardDeck build() { // Builds a card deck with the specified builder settings.
            CardDeck deck = new CardDeck();
            // We have access to private fields of the CardDeck class because this Builder class is an internal class of that CardDeck class.
            deck.knightValueOverride = this.knightValueOverride;
            deck.queenValueOverride = this.queenValueOverride;
            deck.kingValueOverride = this.kingValueOverride;
            deck.aceValueOverride = this.aceValueOverride;

            deck.create(); // Private method to apply all the above settings to the card deck.
            return deck;
        }
    }

    private List<Card> deck;

    private int knightValueOverride = 11;
    private int queenValueOverride = 12;
    private int kingValueOverride = 13;
    private int aceValueOverride = 14;

    // Generate a card deck with random cards
    public CardDeck(int amountOfCards) {
        Random rand = new Random();
        this.deck = new ArrayList<>();
        for (int i = 0; i < amountOfCards; i++) {
            deck.add(new Card(Card.CardSymbol.values()[rand.nextInt(Card.CardSymbol.values().length)],rand.nextInt(13)+1));
        }
    }

    // Generate a standard card deck
    public CardDeck(boolean containsJokers) {
        this.deck = new ArrayList<>();

        int symbolIndex = 0;
        int cardValue = 1;
        for (int i = 1; i <= 52; i++) {
            Card.CardSymbol currentSymbol = Card.CardSymbol.values()[symbolIndex];

            Card card = new Card(currentSymbol, cardValue);
            this.deck.add(card);

            cardValue++;
            if (i % 13 == 0) {
                symbolIndex++;
                cardValue = 1;
            }
        }

        if (containsJokers) {
            for (int i = 0; i < 3; i++) {
                Card card = new Card(Card.CardSymbol.Joker, 69);
                this.deck.add(card);
            }
        }
    }

    // Only the builder class should have access to an empty constructor.
    private CardDeck() {

    }

    // Applies all settings specified in the CardDeck class to actually create a new card deck
    private void create() {
        this.deck = new ArrayList<>();

        int symbolIndex = 0;
        int cardValue = 1;
        for (int i = 1; i <= 52; i++) {
            Card.CardSymbol currentSymbol = Card.CardSymbol.values()[symbolIndex];

            int tempCardValue = cardValue;
            if (cardValue == 1) {
                cardValue = aceValueOverride;
            }
            else if (cardValue == 11) {
                cardValue = knightValueOverride;
            }
            else if (cardValue == 12) {
                cardValue = queenValueOverride;
            }
            else if (cardValue == 13) {
                cardValue = kingValueOverride;
            }

            Card card = new Card(currentSymbol, cardValue);
            this.deck.add(card);

            cardValue = tempCardValue;

            cardValue++;
            if (i % 13 == 0) {
                symbolIndex++;
                cardValue = 1;
            }
        }
    }

    // Add a card to the deck.
    public void addCard(Card card) {
        deck.add(card);
    }

    // Draw a single card.
    public Card drawCard() {
        Random random = new Random();
        return this.deck.get(random.nextInt(this.deck.size()));
    }

    // Draw multiple cards
    public Card[] drawCards(int amount) {
        Card[] cards = new Card[amount];
        for (int i = 0; i < amount; i++) {
            cards[i] = drawCard();
        }
        return cards;
    }

    // Shuffle the card deck.
    public void shuffle() {
        Collections.shuffle(this.deck);
    }

}
