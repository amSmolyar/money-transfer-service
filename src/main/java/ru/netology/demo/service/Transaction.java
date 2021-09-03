package ru.netology.demo.service;

import ru.netology.demo.requestObjects.Amount;

public class Transaction {
    private String cardFromNumber;
    private String cardToNumber;
    private Amount amount;

    public Transaction(String cardFromNumber, String cardToNumber, Amount amount) {
        this.cardFromNumber = cardFromNumber;
        this.cardToNumber = cardToNumber;
        this.amount = amount;
    }

    public String getCardFromNumber() {
        return cardFromNumber;
    }

    public void setCardFromNumber(String cardFromNumber) {
        this.cardFromNumber = cardFromNumber;
    }

    public String getCardToNumber() {
        return cardToNumber;
    }

    public void setCardToNumber(String cardToNumber) {
        this.cardToNumber = cardToNumber;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return amount.getValue() + " " + amount.getCurrency() +
                " from card number " + cardFromNumber +
                " to card number " + cardToNumber;
    }
}
