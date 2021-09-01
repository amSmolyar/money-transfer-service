package ru.netology.demo.repository;

import ru.netology.demo.card.Card;
import ru.netology.demo.card.CardBase;

import java.util.concurrent.ConcurrentHashMap;

public class Repository {
    private ConcurrentHashMap<String, Card> cardMap;

    public Repository() {
        CardBase cardBase = new CardBase();
        cardMap = cardBase.getCardMap();
    }

    public Card getCard(String number) {
        return cardMap.getOrDefault(number, null);
    }


}
