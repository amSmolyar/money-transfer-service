package ru.netology.demo.card;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.Pattern;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Card {
    @Pattern(regexp = "(\\d{16})")
    private final String number;

    @Pattern(regexp = "(\\d{2})(\\/)(\\d{2})")
    private final String validTill;

    @Pattern(regexp = "(\\d{3})")
    private final String cvv;

    @Pattern(regexp = "RUR")
    private final String currency;

    @Pattern(regexp = "(\\d+)")
    private int balance;

    private static Lock lock;
    private static Condition condition;

    @JsonCreator
    public Card(String number, String validTill, String cvv, String currency, int balance) {
        this.number = number;
        this.validTill = validTill;
        this.cvv = cvv;
        this.currency = currency;
        this.balance = balance;

        lock = new ReentrantLock(true);
        condition = lock.newCondition();
    }

    public String getNumber() {
        return number;
    }

    public String getValidTill() {
        return validTill;
    }

    public String getCvv() {
        return cvv;
    }

    public String getCurrency() {
        return currency;
    }

    public int getBalance() {
        return balance;
    }

    public boolean chargeMoney(int amount) {
        lock.lock();
        boolean res = false;
        if (amount > 0) {
            this.balance = this.balance + amount;
            res = true;
        }
        condition.signalAll();
        lock.unlock();
        return res;
    }

    public boolean offMoney(int amount) {
        lock.lock();
        boolean res = false;
        if ((amount > 0) && (this.balance >= amount)) {
            this.balance = this.balance - amount;
            res = true;
        }
        condition.signalAll();
        lock.unlock();
        return res;
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
