package com.company.shared.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CardDeck {

    private List<Card> deck;

    // Generate a card deck with random cards
    public CardDeck(int amountOfCards) {
        this.deck = new ArrayList<>();
        for (int i = 0; i < amountOfCards; i++) {
            deck.add(new Card());
        }
    }

    // Generate a standard card deck
    public CardDeck() {
        this.deck = new ArrayList<>();

        int symbolIndex = 0;
        for (int i = 1; i <= 52; i++) {
            if (i % 13 == 0) {
                symbolIndex++;
            }
            Card.CardSymbol currentSymbol = Card.CardSymbol.values()[symbolIndex];

            Card card = new Card(currentSymbol, i);
            this.deck.add(card);

        }
    }

    public void addCard(Card card) {
        deck.add(card);
    }

    public Card pickRandomCard() {
        Random random = new Random();
        return this.deck.get(random.nextInt(this.deck.size()));
    }

    public void shuffle() {
        Collections.shuffle(this.deck);
    }

}
