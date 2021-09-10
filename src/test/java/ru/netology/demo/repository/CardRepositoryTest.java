package ru.netology.demo.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.demo.dto.Card;
import ru.netology.demo.pojo.CardBase;
import ru.netology.demo.config.CardFileParam;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class CardRepositoryTest {

    @InjectMocks
    CardFileParam cardFileParam;

    @InjectMocks
    CardBase cardBase;

    @InjectMocks
    CardRepository cardRepository;

    private ConcurrentHashMap<String, Card> createMap() {
        ConcurrentHashMap<String, Card> map = new ConcurrentHashMap<>();
        map.put("1234567890123456", new Card("1234567890123456", "01/23", "123", "RUR", 1000));
        map.put("2345678901234567", new Card("2345678901234567", "02/23", "234", "RUR", 2000));
        map.put("3456789012345678", new Card("3456789012345678", "03/23", "345", "RUR", 3000));
        map.put("4567890123456789", new Card("4567890123456789", "04/23", "456", "RUR", 4000));
        return map;
    }

    // ==========================================================================================
    // Проверка конструктора и того, что данные правильно считываются из файла в карту:

    @ParameterizedTest
    @ValueSource(strings = {"1234567890123456", "2345678901234567", "3456789012345678", "4567890123456789"})
    void test_repositoryConstructor_getCard(String number) {
        Map<String, Card> mapActual = cardRepository.getCardMap();

        Card actual = cardRepository.getCard(number).orElse(null);
        Card expected = createMap().get(number);

        assertTrue(expected.equals(actual));
        assertTrue(mapActual.size() == 4);
    }

    // ==========================================================================================
    // Проверка метода chargeMoney класса Repository на то, что при
    // правильных входных данных он возвращает true:

    @ParameterizedTest
    @ValueSource(strings = {"1234567890123456", "2345678901234567", "3456789012345678", "4567890123456789"})
    void test_chargeMoney_true_correctDataIn(String number) {
        assertTrue(cardRepository.chargeMoney(number, 100));
        Map<String, Card> mapActual = cardRepository.getCardMap();
        Card actual = cardRepository.getCard(number).orElse(null);
        Card atStart = createMap().get(number);
        int newBalance = atStart.getBalance() + 100;
        Card expected = new Card(atStart.getNumber(), atStart.getValidTill(), atStart.getCvv(), atStart.getCurrency(), newBalance);

        assertTrue(expected.equals(actual));
    }

    // ==========================================================================================
    // Проверка метода chargeMoney класса Repository на то, что при
    // неправильно указанном номере карты он возвращает false:

    @ParameterizedTest
    @ValueSource(strings = {"5234567890123456", "6345678901234567", "7456789012345678", "8567890123456789"})
    void test_chargeMoney_false_wrongNumber(String number) {
        assertFalse(cardRepository.chargeMoney(number, 100));
    }

    // ==========================================================================================
    // Проверка метода chargeMoney класса Repository на то, что при
    // не положительной сумме перевода он возвращает false:

    @ParameterizedTest
    @ValueSource(strings = {"1234567890123456", "2345678901234567", "3456789012345678", "4567890123456789"})
    void test_chargeMoney_false_wrongAmount(String number) {
        assertFalse(cardRepository.chargeMoney(number, -100));
        assertFalse(cardRepository.chargeMoney(number, 0));
        assertFalse(cardRepository.chargeMoney(number, -1));
    }

    // ==========================================================================================
    // Проверка метода offMoney класса Repository на то, что при
    // правильных входных данных он возвращает true:

    @ParameterizedTest
    @ValueSource(strings = {"1234567890123456", "2345678901234567", "3456789012345678", "4567890123456789"})
    void test_offMoney_true_correctDataIn(String number) {
        assertTrue(cardRepository.offMoney(number, 100));

        Card actual = cardRepository.getCard(number).orElse(null);
        Card atStart = createMap().get(number);
        int newBalance = atStart.getBalance() - 100;
        Card expected = new Card(atStart.getNumber(), atStart.getValidTill(), atStart.getCvv(), atStart.getCurrency(), newBalance);

        assertTrue(expected.equals(actual));

        assertTrue(cardRepository.offMoney(number, 900));

        actual = cardRepository.getCard(number).orElse(null);
        newBalance = newBalance - 900;
        expected = new Card(atStart.getNumber(), atStart.getValidTill(), atStart.getCvv(), atStart.getCurrency(), newBalance);

        assertTrue(expected.equals(actual));
    }

    // ==========================================================================================
    // Проверка метода offMoney класса Repository на то, что при
    // неправильно указанном номере карты он возвращает false:

    @ParameterizedTest
    @ValueSource(strings = {"5234567890123456", "6345678901234567", "7456789012345678", "8567890123456789"})
    void test_offMoney_false_wrongNumber(String number) {
        assertFalse(cardRepository.offMoney(number, 100));
        assertFalse(cardRepository.offMoney(number, 500));
        assertFalse(cardRepository.offMoney(number, 1000));
    }

    // ==========================================================================================
    // Проверка метода offMoney класса Repository на то, что при
    // не положительной сумме перевода он возвращает false:

    @ParameterizedTest
    @ValueSource(strings = {"1234567890123456", "2345678901234567", "3456789012345678", "4567890123456789"})
    void test_offMoney_false_wrongAmount(String number) {
        assertFalse(cardRepository.offMoney(number, -100));
        assertFalse(cardRepository.offMoney(number, 0));
        assertFalse(cardRepository.offMoney(number, -1));

        if (number.equals("1234567890123456"))
            assertFalse(cardRepository.offMoney(number, 1001));
        else if (number.equals("2345678901234567"))
            assertFalse(cardRepository.offMoney(number, 2001));
        else if (number.equals("3456789012345678"))
            assertFalse(cardRepository.offMoney(number, 3001));
        else if (number.equals("4567890123456789"))
            assertFalse(cardRepository.offMoney(number, 4001));


        assertFalse(cardRepository.offMoney(number, 10000));
    }
}