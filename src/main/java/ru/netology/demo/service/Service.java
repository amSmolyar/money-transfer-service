package ru.netology.demo.service;

import ru.netology.demo.card.Card;
import ru.netology.demo.repository.Repository;
import ru.netology.demo.requestObjects.TransferParameters;

public class Service {
    private Repository repository;

    public Service(Repository repository) {
        this.repository = repository;
    }

    public Card getFromCard(TransferParameters parameters) {
        return repository.getCard(parameters.getCardFromNumber());
    }
}
