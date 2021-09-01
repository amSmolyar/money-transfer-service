package ru.netology.demo.card;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public class Card {
    private String number;
    private String validTill;
    private String cvv;
    private String currency;
    private int balance;

    @JsonCreator
    public Card(String number, String validTill, String cvv, String currency, int balance) {
        this.number = number;
        this.validTill = validTill;
        this.cvv = cvv;
        this.currency = currency;
        this.balance = balance;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getValidTill() {
        return validTill;
    }

    public void setValidTill(String validTill) {
        this.validTill = validTill;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return number.equals(card.number) &&
                validTill.equals(card.validTill) &&
                cvv.equals(card.cvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, validTill, cvv);
    }

}
