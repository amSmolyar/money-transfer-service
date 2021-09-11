package ru.netology.demo.repository;

import ru.netology.demo.dto.Card;
import ru.netology.demo.pojo.CardBase;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CardRepository {
    private final ConcurrentHashMap<String, Card> cardMap;

    public CardRepository() {
        CardBase cardBase = new CardBase();
        cardMap = cardBase.getCardMap();
    }

    public Optional<Card> getCard(String number) {
        return (cardMap.containsKey(number)) ? Optional.of(cardMap.get(number)) : Optional.empty();
    }

    public boolean chargeMoney(String cardNumber, int amount) {
        if (!cardMap.containsKey(cardNumber) || (amount <= 0))
            return false;

        Card card = cardMap.get(cardNumber);
        card.chargeMoney(amount);
        cardMap.put(cardNumber,card);
        return true;
    }

    public boolean offMoney(String cardNumber, int amount) {
        if (!cardMap.containsKey(cardNumber))
            return false;

        Card card = cardMap.get(cardNumber);
        if (!card.offMoney(amount))
            return false;

        cardMap.put(cardNumber,card);
        return true;
    }

    public ConcurrentHashMap<String, Card> getCardMap() {
        return cardMap;
    }
}
