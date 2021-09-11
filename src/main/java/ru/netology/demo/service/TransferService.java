package ru.netology.demo.service;

import ru.netology.demo.dto.*;
import ru.netology.demo.controller.LoggerController;
import ru.netology.demo.exceptions.CardNotFoundException;
import ru.netology.demo.exceptions.CurrencyMismatchException;
import ru.netology.demo.exceptions.FailedOperationException;
import ru.netology.demo.exceptions.InsufficientFundsException;
import ru.netology.demo.pojo.Transaction;
import ru.netology.demo.repository.CardRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TransferService {
    private final CardRepository cardRepository;

    private final AtomicInteger operationIdInt;
    private final ConcurrentHashMap<String, Transaction> operationMap;

    public TransferService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        this.operationIdInt = new AtomicInteger(0);
        this.operationMap = new ConcurrentHashMap<>();
    }

    public String requestTransfer(TransferParameters parameters) {
        String cardFromNumber = checkCardFrom(parameters);
        String cardToNumber = checkCardTo(parameters);
        Amount transferAmount = parameters.getAmount();

        String operationId = String.valueOf(operationIdInt.incrementAndGet());
        operationMap.put(operationId, new Transaction(cardFromNumber, cardToNumber, transferAmount));

        return operationId;
    }

    public String checkCardFrom(TransferParameters parameters) {
        // Существует ли в базе карта from:
        Card cardFromRepo = cardRepository.getCard(parameters.getCardFromNumber())
                .orElseThrow(() -> new CardNotFoundException("there is no card with number " + parameters.getCardFromNumber() + " in the database"));
        if (!cardFromRepo.getValidTill().equals(parameters.getCardFromValidTill()))
            throw new CardNotFoundException("the card " + parameters.getCardFromNumber() + " expiration date is incorrect");
        if (!cardFromRepo.getCvv().equals(parameters.getCardFromCVV()))
            throw new CardNotFoundException("the card " + parameters.getCardFromNumber() + " cvv code is incorrect");

        // Валюта не совпадает
        if (!cardFromRepo.getCurrency().equals(parameters.getAmount().getCurrency()))
            throw new CurrencyMismatchException("card " + parameters.getCardFromNumber() + " currency is not suitable for transfer");

        // на счету недостаточно средств для перевода:
        if (parameters.getAmount().getValue() > cardFromRepo.getBalance())
            throw new InsufficientFundsException("there are not enough funds on the card " + parameters.getCardFromNumber() + " for transfer");

        return cardFromRepo.getNumber();
    }

    public String checkCardTo(TransferParameters parameters) {
        // Существует ли в базе карта to:
        Card cardToRepo = cardRepository.getCard(parameters.getCardToNumber())
                .orElseThrow(() -> new CardNotFoundException("there is no card with number " + parameters.getCardToNumber() + " in the database"));

        // Валюта не совпадает
        if (!cardToRepo.getCurrency().equals(parameters.getAmount().getCurrency()))
            throw new CurrencyMismatchException("card " + parameters.getCardToNumber() + " currency is not suitable for transfer");

        return cardToRepo.getNumber();
    }


    public String confirmOperation(ConfirmParameters parameters) {
        if (!operationMap.containsKey(parameters.getOperationId()))
            throw new FailedOperationException("There is no operation with operationId = " + parameters.getOperationId() + ". The operation could not be completed.");

        Transaction operation = operationMap.get(parameters.getOperationId());
        if (!cardRepository.offMoney(operation.getCardFromNumber(), operation.getAmount().getValue()))
            throw new InsufficientFundsException("there are not enough funds on the card " + operation.getCardFromNumber() + " to complete operation with operationId = " + parameters.getOperationId());

        cardRepository.chargeMoney(operation.getCardToNumber(), operation.getAmount().getValue());
        operationMap.remove(parameters.getOperationId());

        LoggerController.getLogger().info("The operation of transfer " + operation.toString() + " was successfully completed");
        return parameters.getOperationId();
    }

    public AtomicInteger getOperationIdInt() {
        return operationIdInt;
    }

    public ConcurrentHashMap<String, Transaction> getOperationMap() {
        return operationMap;
    }
}
