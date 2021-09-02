package ru.netology.demo.exceptions;

public class InsufficientFundsException extends RuntimeException {
    private int id;

    public InsufficientFundsException(String msg) {
        super(msg);
        id = 4;
    }

    public int getId() {
        return id;
    }
}
