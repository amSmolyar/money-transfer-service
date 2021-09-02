package ru.netology.demo.service;

public class Transaction {
    private String cardFromNumber;
    private String cardToNumber;
    private int transferAmount;

    public Transaction(String cardFromNumber, String cardToNumber, int transferAmount) {
        this.cardFromNumber = cardFromNumber;
        this.cardToNumber = cardToNumber;
        this.transferAmount = transferAmount;
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

    public int getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(int transferAmount) {
        this.transferAmount = transferAmount;
    }
}
