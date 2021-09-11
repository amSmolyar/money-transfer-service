package ru.netology.demo.pojo;

import ru.netology.demo.dto.Card;
import ru.netology.demo.config.CardFileParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class CardBase {
    private final ConcurrentHashMap<String, Card> cardMap;
    private final CardFileParam cardFile;

    private final DateFormat dateFormat;
    private final Date date;

    public CardBase() {
        cardMap = new ConcurrentHashMap<>();
        cardFile = new CardFileParam();

        this.dateFormat = new SimpleDateFormat("MM/yy");
        this.date = new Date();
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

                if (parts[1].trim().matches("(\\d{2})(\\/)(\\d{2})")) {
                    validTill = checkTillData(parts[1].trim(), dateFormat.format(date));
                } else
                    throw new RuntimeException("Неправильный формат записи данных в файле" + filename);

                if (parts[2].trim().matches("(\\d{3})"))
                    cvv = parts[2].trim();
                else
                    throw new RuntimeException("Неправильный формат записи данных в файле" + filename);

                if (parts[3].trim().equals("RUR"))
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

    private String checkTillData(String tillData, String currentData) {
        String[] validTillParts = tillData.split("/");
        String[] currentDataParts = currentData.split("/");

        if (Integer.parseInt(validTillParts[0]) > 12)
            throw new RuntimeException("Неправильный формат записи даты");

        int tillMonth = Integer.parseInt(validTillParts[0]);
        int currentMonth = Integer.parseInt(currentDataParts[0]);

        int tillYear = Integer.parseInt(validTillParts[1]);
        int currentYear = Integer.parseInt(currentDataParts[1]);

        if ((tillYear > currentYear) || ((tillYear == currentYear) && (tillMonth >= currentMonth)))
            return tillData;
        else
            throw new RuntimeException("Неправильный формат записи даты");
    }

    public ConcurrentHashMap<String, Card> getCardMap() {
        return cardMap;
    }
}
