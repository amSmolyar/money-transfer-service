package ru.netology.demo.exceptions;

public class CardNotFoundException extends RuntimeException {
    private final int id;

    public CardNotFoundException(String msg) {
        super(msg);
        id = 1;
    }

    public int getId() {
        return id;
    }
}
