package ru.netology.demo.card;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.netology.demo.config.CardFileParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class CardBase {
    private ConcurrentHashMap<String, Card> cardMap;
    private CardFileParam cardFile;

    public CardBase() {
        cardMap = new ConcurrentHashMap<>();
        cardFile = new CardFileParam();
        readCardBaseFile();
    }

    private void addCard(Card newCard) {
        if (!cardMap.containsKey(newCard.getNumber()))
            cardMap.put(newCard.getNumber(), newCard);
    }

    private void readCardBaseFile() {
        String filepath = cardFile.getFilepath();
        String filename = cardFile.getFilename();

        File file = new File(filepath, filename);
        String number;
        String validTill;
        String cvv;
        String currency;
        int balance;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 5)
                    throw new RuntimeException("Неправильный формат записи данных в файле" + filename);

                if (parts[0].trim().matches("(\\d{16})"))
                    number = parts[0].trim();
                else
                    throw new RuntimeException("Неправильный формат записи данных в файле" + filename);

                if (parts[1].trim().matches("(\\d{4})"))
                    validTill = parts[1].trim();
                else
                    throw new RuntimeException("Неправильный формат записи данных в файле" + filename);

                if (parts[2].trim().matches("(\\d{3})"))
                    cvv = parts[2].trim();
                else
                    throw new RuntimeException("Неправильный формат записи данных в файле" + filename);

                if (parts[3].trim().equals("rub"))
                    currency = parts[3].trim();
                else
                    throw new RuntimeException("Сервис поддерживает только переводы в рублях");

                if (parts[4].trim().matches("(\\d+)"))
                    balance = Integer.parseInt(parts[4].trim());
                else
                    throw new RuntimeException("Баланс должен быть представлен целым числом");

                addCard(new Card(number, validTill, cvv, currency, balance));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public ConcurrentHashMap<String, Card> getCardMap() {
        return cardMap;
    }
}
