package ru.netology.demo.repository;

import ru.netology.demo.card.Card;
import ru.netology.demo.card.CardBase;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Repository {
    private ConcurrentHashMap<String, Card> cardMap;

    public Repository() {
        CardBase cardBase = new CardBase();
        cardMap = cardBase.getCardMap();
    }

    public Optional<Card> getCard(String number) {
        return (cardMap.containsKey(number)) ? Optional.of(cardMap.get(number)) : Optional.empty();
    }

    public void chargeMoney(String cardNumber, int amount) {
        Card card = cardMap.get(cardNumber);
        card.chargeMoney(amount);
        cardMap.put(cardNumber,card);
    }

    public boolean offMoney(String cardNumber, int amount) {
        Card card = cardMap.get(cardNumber);
        if (!card.offMoney(amount))
            return false;

        cardMap.put(cardNumber,card);
        return true;
    }

}
