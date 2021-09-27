package nu.berggame.shared.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CardDeck {

    public static class Builder {

        private int knightValueOverride = 11;
        private int queenValueOverride = 12;
        private int kingValueOverride = 13;
        private int aceValueOverride = 14;

        public Builder pictureCardsAsTen(boolean enabled) {
            this.kingValueOverride = 10;
            this.queenValueOverride = 10;
            this.knightValueOverride = 10;

            return this;
        }

        public Builder overrideKnightValue(int newValue) {
            this.knightValueOverride = newValue;

            return this;
        }

        public Builder overrideQueenValue(int newValue) {
            this.queenValueOverride = newValue;

            return this;
        }

        public Builder overrideKingValue(int newValue) {
            this.kingValueOverride = newValue;

            return this;
        }

        public Builder overrideAceValue(int newValue) {
            this.aceValueOverride = newValue;

            return this;
        }

        public CardDeck build() {
            CardDeck deck = new CardDeck();
            deck.knightValueOverride = this.knightValueOverride;
            deck.queenValueOverride = this.queenValueOverride;
            deck.kingValueOverride = this.kingValueOverride;
            deck.aceValueOverride = this.aceValueOverride;

            deck.create();
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
        this.deck = new ArrayList<>();
        for (int i = 0; i < amountOfCards; i++) {
            deck.add(new Card());
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

    private CardDeck() {

    }

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


    public void addCard(Card card) {
        deck.add(card);
    }

    public Card drawCard() {
        Random random = new Random();
        return this.deck.get(random.nextInt(this.deck.size()));
    }

    public Card[] drawCards(int amount) {
        Card[] cards = new Card[amount];
        for (int i = 0; i < amount; i++) {
            cards[i] = drawCard();
        }
        return cards;
    }

    public void shuffle() {
        Collections.shuffle(this.deck);
    }

}
