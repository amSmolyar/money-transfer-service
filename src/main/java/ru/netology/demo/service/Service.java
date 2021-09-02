package ru.netology.demo.service;

import ru.netology.demo.card.Card;
import ru.netology.demo.exceptions.CardNotFoundException;
import ru.netology.demo.exceptions.CurrencyMismatchException;
import ru.netology.demo.exceptions.FailedOperationException;
import ru.netology.demo.exceptions.InsufficientFundsException;
import ru.netology.demo.repository.Repository;
import ru.netology.demo.requestObjects.ConfirmParameters;
import ru.netology.demo.requestObjects.TransferParameters;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Service {
    private Repository repository;

    private AtomicInteger operationIdInt;
    private ConcurrentHashMap<String, Transaction> operationMap;

    public Service(Repository repository) {
        this.repository = repository;
        this.operationIdInt = new AtomicInteger(0);
        this.operationMap = new ConcurrentHashMap<>();
    }

    public String requestTransfer(TransferParameters parameters) {
        String cardFromNumber = checkCardFrom(parameters);
        String cardToNumber = checkCardTo(parameters);
        int transferAmount = parameters.getAmount().getValue();

        String operationId = String.valueOf(operationIdInt.incrementAndGet());
        operationMap.put(operationId, new Transaction(cardFromNumber, cardToNumber, transferAmount));

        return operationId;
    }

    private String checkCardFrom(TransferParameters parameters) {
        // Существует ли в базе карта from:
        Card cardFromRepo = repository.getCard(parameters.getCardFromNumber())
                .orElseThrow(() -> new CardNotFoundException("there is no such card in the database"));
        if (!cardFromRepo.getValidTill().equals(parameters.getCardFromValidTill()))
            throw new CardNotFoundException("there is no such card in the database");
        if (!cardFromRepo.getCvv().equals(parameters.getCardFromCVV()))
            throw new CardNotFoundException("there is no such card in the database");

        // Валюта не совпадает
        if (!cardFromRepo.getCurrency().equals(parameters.getAmount().getCurrency()))
            throw new CurrencyMismatchException("currency is not suitable for transfer");

        // на счету недостаточно средств для перевода:
        if (parameters.getAmount().getValue() > cardFromRepo.getBalance())
            throw new InsufficientFundsException("there are not enough funds on the card");

        return cardFromRepo.getNumber();
    }

    private String checkCardTo(TransferParameters parameters) {
        // Существует ли в базе карта to:
        Card cardToRepo = repository.getCard(parameters.getCardToNumber())
                .orElseThrow(() -> new CardNotFoundException("there is no such card in the database"));

        // Валюта не совпадает
        if (!cardToRepo.getCurrency().equals(parameters.getAmount().getCurrency()))
            throw new CurrencyMismatchException("currency is not suitable for transfer");

        return cardToRepo.getNumber();
    }


    public String confirmOperation(ConfirmParameters parameters) {
        if (!operationMap.containsKey(parameters.getOperationId()))
            throw new FailedOperationException("The operation could not be completed");

        Transaction operation = operationMap.get(parameters.getOperationId());
        if (!repository.offMoney(operation.getCardFromNumber(), operation.getTransferAmount()))
            throw new InsufficientFundsException("there are not enough funds on the card");

        repository.chargeMoney(operation.getCardToNumber(), operation.getTransferAmount());
        operationMap.remove(parameters.getOperationId());
        return parameters.getOperationId();
    }
}
