package ru.netology.demo.exceptions;

public class FailedOperationException extends RuntimeException {
    private int id;

    public FailedOperationException(String msg) {
        super(msg);
        id = 3;
    }

    public int getId() {
        return id;
    }
}
