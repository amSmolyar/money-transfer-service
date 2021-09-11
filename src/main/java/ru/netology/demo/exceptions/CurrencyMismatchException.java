package ru.netology.demo.exceptions;

public class CurrencyMismatchException extends RuntimeException {
    private final int id;

    public CurrencyMismatchException(String msg) {
        super(msg);
        id = 2;
    }

    public int getId() {
        return id;
    }
}
