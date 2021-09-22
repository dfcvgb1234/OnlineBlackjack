package com.company.shared.cards;

import com.company.shared.Component;
import com.company.shared.messages.CardMessage;
import com.company.shared.messages.Message;

public class Card implements Component {

    private CardSymbol cardSymbol;
    private int cardValue;

    private boolean hasSpecialName;
    private CardSpecialValue specialValue;

    public Card(CardDeck deck) {
        deck.addCard(this);
    }

    public Card (CardSymbol symbol, int cardValue) {
        this.cardSymbol = symbol;
        this.cardValue = cardValue;


    }

    public Card() {

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

    public Class<? extends Message> getMessageType() {
        return CardMessage.class;
    }

    public enum CardSymbol {
        Club,
        Diamond,
        Heart,
        Spade
    }

    public enum CardSpecialValue {
        Ace,
        Jack,
        Queen,
        King
    }
}


