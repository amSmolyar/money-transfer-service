package ru.netology.demo.exceptions;

public class CurrencyMismatchException extends RuntimeException {
    private int id;

    public CurrencyMismatchException(String msg) {
        super(msg);
        id = 2;
    }

    public int getId() {
        return id;
    }
}
