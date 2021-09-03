package ru.netology.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.demo.exceptions.*;
import ru.netology.demo.responseObjects.ExceptionResponse;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    private static final int RUNTIME_EXCEPTION_ID = 10;

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlerCardNotFound(CardNotFoundException e) {
        ExceptionResponse resp = new ExceptionResponse(e.getMessage(), e.getId());
        LoggerController.getLogger().info(e.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrencyMismatchException.class)
    public ResponseEntity<ExceptionResponse> handlerCurrencyMismatch(CurrencyMismatchException e) {
        ExceptionResponse resp = new ExceptionResponse(e.getMessage(), e.getId());
        LoggerController.getLogger().info(e.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FailedOperationException.class)
    public ResponseEntity<ExceptionResponse> handlerFailedOperation(FailedOperationException e) {
        ExceptionResponse resp = new ExceptionResponse(e.getMessage(), e.getId());
        LoggerController.getLogger().info(e.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ExceptionResponse> handlerInsufficientFunds(InsufficientFundsException e) {
        ExceptionResponse resp = new ExceptionResponse(e.getMessage(), e.getId());
        LoggerController.getLogger().info(e.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handlerRuntime(RuntimeException e) {
        ExceptionResponse resp = new ExceptionResponse(e.getMessage(), RUNTIME_EXCEPTION_ID);
        LoggerController.getLogger().info(e.getMessage());
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
