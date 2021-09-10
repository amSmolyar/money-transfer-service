package ru.netology.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.demo.dto.*;
import ru.netology.demo.exceptions.CardNotFoundException;
import ru.netology.demo.exceptions.CurrencyMismatchException;
import ru.netology.demo.exceptions.FailedOperationException;
import ru.netology.demo.exceptions.InsufficientFundsException;
import ru.netology.demo.pojo.Transaction;
import ru.netology.demo.repository.CardRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class TransferServiceTest {

    @InjectMocks
    TransferService transferService;

    @Mock
    CardRepository cardRepository;

    private Map<String, Card> createMap() {
        Map<String, Card> mapRepo = new HashMap<>();
        mapRepo.put("1234567890123456", new Card("1234567890123456", "01/23", "123", "RUR", 1000));
        mapRepo.put("2345678901234567", new Card("2345678901234567", "02/23", "234", "RUR", 2000));
        mapRepo.put("3456789012345678", new Card("3456789012345678", "03/23", "345", "RUR", 3000));
        mapRepo.put("4567890123456789", new Card("4567890123456789", "04/23", "456", "RUR", 4000));
        return mapRepo;
    }

    // ==========================================================================================
    // Проверка корректности работы метода checkCardFrom класса Service
    // для корректных входных данных

    public static Stream<TransferParameters> getTransportParameters_correctValues() {
        TransferParameters[] transferParametersArray = new TransferParameters[4];

        transferParametersArray[0] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(100, "RUR"));
        transferParametersArray[1] = new TransferParameters("2345678901234567", "02/23", "234", "3456789012345678", new Amount(100, "RUR"));
        transferParametersArray[2] = new TransferParameters("3456789012345678", "03/23", "345", "4567890123456789", new Amount(100, "RUR"));
        transferParametersArray[3] = new TransferParameters("4567890123456789", "04/23", "456", "1234567890123456", new Amount(100, "RUR"));

        return Arrays.stream(transferParametersArray);
    }

    @ParameterizedTest
    @MethodSource("getTransportParameters_correctValues")
    void test_checkCardFrom_withoutErrors(TransferParameters transferParameters) {
        Map<String, Card> mapRepo = createMap();

        Optional<Card> cardFromRepo = (mapRepo.containsKey(transferParameters.getCardFromNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardFromNumber())) : Optional.empty();

        when(cardRepository.getCard(transferParameters.getCardFromNumber()))
                .thenReturn(cardFromRepo);

        Card cardExpected = mapRepo.get(transferParameters.getCardFromNumber());
        String cardNumberExpected = cardExpected.getNumber();

        String cardNumberActual = transferService.checkCardFrom(transferParameters);

        assertDoesNotThrow(() ->
                transferService.checkCardFrom(transferParameters));

        assertEquals(cardNumberExpected, cardNumberActual);
    }

    // ==========================================================================================
    // Проверка работы метода checkCardFrom класса Service.
    // Проверяется выброс исключения CardNotFoundException (и текст его сообщения) для
    // несуществующего в базе номера карты отправителя

    public static Stream<TransferParameters> getTransportParameters_falseCardFromNumber() {
        TransferParameters[] transferParametersArray = new TransferParameters[4];

        transferParametersArray[0] = new TransferParameters("6234567890123456", "01/23", "123", "2345678901234567", new Amount(100, "RUR"));
        transferParametersArray[1] = new TransferParameters("7345678901234567", "02/23", "234", "3456789012345678", new Amount(100, "RUR"));
        transferParametersArray[2] = new TransferParameters("8456789012345678", "03/23", "345", "4567890123456789", new Amount(100, "RUR"));
        transferParametersArray[3] = new TransferParameters("9567890123456789", "04/23", "456", "1234567890123456", new Amount(100, "RUR"));

        return Arrays.stream(transferParametersArray);
    }

    @ParameterizedTest
    @MethodSource("getTransportParameters_falseCardFromNumber")
    void test_checkCardFrom_CardNotFoundException_number(TransferParameters transferParameters) {
        Map<String, Card> mapRepo = createMap();

        Optional<Card> cardFromRepo = (mapRepo.containsKey(transferParameters.getCardFromNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardFromNumber())) : Optional.empty();

        when(cardRepository.getCard(transferParameters.getCardFromNumber()))
                .thenReturn(cardFromRepo);

        CardNotFoundException e = assertThrows(CardNotFoundException.class, () ->
                transferService.checkCardFrom(transferParameters));

        String message = e.getMessage();
        assertTrue(message.equals("there is no card with number " + transferParameters.getCardFromNumber() + " in the database"));

    }


    // ==========================================================================================
    // Проверка работы метода checkCardFrom класса Service.
    // Проверяется выброс исключения CardNotFoundException (и текст его сообщения) для
    // номера карты отправителя с неправильно введенным сроком действия

    public static Stream<TransferParameters> getTransportParameters_falseCardFromTillData() {
        TransferParameters[] transferParametersArray = new TransferParameters[4];

        transferParametersArray[0] = new TransferParameters("1234567890123456", "06/23", "123", "2345678901234567", new Amount(100, "RUR"));
        transferParametersArray[1] = new TransferParameters("2345678901234567", "07/23", "234", "3456789012345678", new Amount(100, "RUR"));
        transferParametersArray[2] = new TransferParameters("3456789012345678", "08/23", "345", "4567890123456789", new Amount(100, "RUR"));
        transferParametersArray[3] = new TransferParameters("4567890123456789", "09/23", "456", "1234567890123456", new Amount(100, "RUR"));

        return Arrays.stream(transferParametersArray);
    }

    @ParameterizedTest
    @MethodSource("getTransportParameters_falseCardFromTillData")
    void test_checkCardFrom_CardNotFoundException_tillData(TransferParameters transferParameters) {
        Map<String, Card> mapRepo = createMap();

        Optional<Card> cardFromRepo = (mapRepo.containsKey(transferParameters.getCardFromNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardFromNumber())) : Optional.empty();

        when(cardRepository.getCard(transferParameters.getCardFromNumber()))
                .thenReturn(cardFromRepo);

        CardNotFoundException e = assertThrows(CardNotFoundException.class, () ->
                transferService.checkCardFrom(transferParameters));

        String message = e.getMessage();
        assertTrue(message.equals("the card " + transferParameters.getCardFromNumber() + " expiration date is incorrect"));

    }

    // ==========================================================================================
    // Проверка работы метода checkCardFrom класса Service.
    // Проверяется выброс исключения CardNotFoundException (и текст его сообщения) для
    // номера карты отправителя с неправильно введенным cvv

    public static Stream<TransferParameters> getTransportParameters_falseCardFromCvv() {
        TransferParameters[] transferParametersArray = new TransferParameters[4];

        transferParametersArray[0] = new TransferParameters("1234567890123456", "01/23", "567", "2345678901234567", new Amount(100, "RUR"));
        transferParametersArray[1] = new TransferParameters("2345678901234567", "02/23", "678", "3456789012345678", new Amount(100, "RUR"));
        transferParametersArray[2] = new TransferParameters("3456789012345678", "03/23", "789", "4567890123456789", new Amount(100, "RUR"));
        transferParametersArray[3] = new TransferParameters("4567890123456789", "04/23", "890", "1234567890123456", new Amount(100, "RUR"));

        return Arrays.stream(transferParametersArray);
    }

    @ParameterizedTest
    @MethodSource("getTransportParameters_falseCardFromCvv")
    void test_checkCardFrom_CardNotFoundException_cvv(TransferParameters transferParameters) {
        Map<String, Card> mapRepo = createMap();

        Optional<Card> cardFromRepo = (mapRepo.containsKey(transferParameters.getCardFromNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardFromNumber())) : Optional.empty();

        when(cardRepository.getCard(transferParameters.getCardFromNumber()))
                .thenReturn(cardFromRepo);

        CardNotFoundException e = assertThrows(CardNotFoundException.class, () ->
                transferService.checkCardFrom(transferParameters));

        String message = e.getMessage();
        assertTrue(message.equals("the card " + transferParameters.getCardFromNumber() + " cvv code is incorrect"));

    }

    // ==========================================================================================
    // Проверка работы метода checkCardFrom класса Service.
    // Проверяется выброс исключения CurrencyMismatchException (и текст его сообщения) в
    // случае, когда валюты карты отправителя и перевода не совпадают

    public static Stream<TransferParameters> getTransportParameters_falseCurrency() {
        TransferParameters[] transferParametersArray = new TransferParameters[4];

        transferParametersArray[0] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(100, "EUR"));
        transferParametersArray[1] = new TransferParameters("2345678901234567", "02/23", "234", "3456789012345678", new Amount(100, "FNR"));
        transferParametersArray[2] = new TransferParameters("3456789012345678", "03/23", "345", "4567890123456789", new Amount(100, "USD"));
        transferParametersArray[3] = new TransferParameters("4567890123456789", "04/23", "456", "1234567890123456", new Amount(100, "FRC"));

        return Arrays.stream(transferParametersArray);
    }

    @ParameterizedTest
    @MethodSource("getTransportParameters_falseCurrency")
    void test_checkCardFrom_CurrencyMismatchException(TransferParameters transferParameters) {
        Map<String, Card> mapRepo = createMap();

        Optional<Card> cardFromRepo = (mapRepo.containsKey(transferParameters.getCardFromNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardFromNumber())) : Optional.empty();

        when(cardRepository.getCard(transferParameters.getCardFromNumber()))
                .thenReturn(cardFromRepo);

        CurrencyMismatchException e = assertThrows(CurrencyMismatchException.class, () ->
                transferService.checkCardFrom(transferParameters));

        String message = e.getMessage();
        assertTrue(message.equals("card " + transferParameters.getCardFromNumber() + " currency is not suitable for transfer"));

    }

    // ==========================================================================================
    // Проверка работы метода checkCardFrom класса Service.
    // Проверяется выброс исключения InsufficientFundsException (и текст его сообщения) в
    // случае, когда средств на карте отправителя недостаточно для перевода

    public static Stream<TransferParameters> getTransportParameters_fundsError() {
        TransferParameters[] transferParametersArray = new TransferParameters[4];

        transferParametersArray[0] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(5000, "RUR"));
        transferParametersArray[1] = new TransferParameters("2345678901234567", "02/23", "234", "3456789012345678", new Amount(16000, "RUR"));
        transferParametersArray[2] = new TransferParameters("3456789012345678", "03/23", "345", "4567890123456789", new Amount(7000, "RUR"));
        transferParametersArray[3] = new TransferParameters("4567890123456789", "04/23", "456", "1234567890123456", new Amount(9999, "RUR"));

        return Arrays.stream(transferParametersArray);
    }

    @ParameterizedTest
    @MethodSource("getTransportParameters_fundsError")
    void test_checkCardFrom_InsufficientFundsException(TransferParameters transferParameters) {
        Map<String, Card> mapRepo = createMap();

        Optional<Card> cardFromRepo = (mapRepo.containsKey(transferParameters.getCardFromNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardFromNumber())) : Optional.empty();

        when(cardRepository.getCard(transferParameters.getCardFromNumber()))
                .thenReturn(cardFromRepo);

        InsufficientFundsException e = assertThrows(InsufficientFundsException.class, () ->
                transferService.checkCardFrom(transferParameters));

        String message = e.getMessage();
        assertTrue(message.equals("there are not enough funds on the card " + transferParameters.getCardFromNumber() + " for transfer"));

    }

    // ==========================================================================================
    // Проверка корректности работы метода checkCardFrom класса Service
    // для корректных входных данных

    @ParameterizedTest
    @MethodSource("getTransportParameters_correctValues")
    void test_checkCardTo_withoutErrors(TransferParameters transferParameters) {
        Map<String, Card> mapRepo = createMap();

        Optional<Card> cardToRepo = (mapRepo.containsKey(transferParameters.getCardToNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardToNumber())) : Optional.empty();

        when(cardRepository.getCard(transferParameters.getCardToNumber()))
                .thenReturn(cardToRepo);

        String cardNumberActual = transferService.checkCardTo(transferParameters);

        assertDoesNotThrow(() ->
                transferService.checkCardTo(transferParameters));

        assertEquals(transferParameters.getCardToNumber(), cardNumberActual);
    }

    // ==========================================================================================
    // Проверка работы метода checkCardTo класса Service.
    // Проверяется выброс исключения CardNotFoundException (и текст его сообщения) для
    // несуществующего в базе номера карты получателя

    public static Stream<TransferParameters> getTransportParameters_falseCardToNumber() {
        TransferParameters[] transferParametersArray = new TransferParameters[4];

        transferParametersArray[0] = new TransferParameters("1234567890123456", "01/23", "123", "5345678901234567", new Amount(100, "RUR"));
        transferParametersArray[1] = new TransferParameters("2345678901234567", "02/23", "234", "6456789012345678", new Amount(100, "RUR"));
        transferParametersArray[2] = new TransferParameters("3456789012345678", "03/23", "345", "7567890123456789", new Amount(100, "RUR"));
        transferParametersArray[3] = new TransferParameters("4567890123456789", "04/23", "456", "8234567890123456", new Amount(100, "RUR"));

        return Arrays.stream(transferParametersArray);
    }

    @ParameterizedTest
    @MethodSource("getTransportParameters_falseCardToNumber")
    void test_checkCardTo_CardNotFoundException_number(TransferParameters transferParameters) {
        Map<String, Card> mapRepo = createMap();

        Optional<Card> cardToRepo = (mapRepo.containsKey(transferParameters.getCardToNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardToNumber())) : Optional.empty();

        when(cardRepository.getCard(transferParameters.getCardToNumber()))
                .thenReturn(cardToRepo);

        CardNotFoundException e = assertThrows(CardNotFoundException.class, () ->
                transferService.checkCardTo(transferParameters));

        String message = e.getMessage();
        assertTrue(message.equals("there is no card with number " + transferParameters.getCardToNumber() + " in the database"));

    }

    // ==========================================================================================
    // Проверка работы метода checkCardTo класса Service.
    // Проверяется выброс исключения CurrencyMismatchException (и текст его сообщения) в
    // случае, когда валюты карты получателя и перевода не совпадают


    @ParameterizedTest
    @MethodSource("getTransportParameters_falseCurrency")
    void test_checkCardTo_CurrencyMismatchException(TransferParameters transferParameters) {
        Map<String, Card> mapRepo = createMap();

        Optional<Card> cardToRepo = (mapRepo.containsKey(transferParameters.getCardToNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardToNumber())) : Optional.empty();

        when(cardRepository.getCard(transferParameters.getCardToNumber()))
                .thenReturn(cardToRepo);

        CurrencyMismatchException e = assertThrows(CurrencyMismatchException.class, () ->
                transferService.checkCardTo(transferParameters));

        String message = e.getMessage();
        assertTrue(message.equals("card " + transferParameters.getCardToNumber() + " currency is not suitable for transfer"));

    }

    // ==========================================================================================
    // Проверка корректности работы метода requestTransfer класса Service
    // для корректных входных данных. Проверяется корректное сохранение в карте параметров транзакции

    public static TransferParameters[] getTransportParametersArray_correctValues() {
        TransferParameters[] transferParametersArray = new TransferParameters[4];

        transferParametersArray[0] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(100, "RUR"));
        transferParametersArray[1] = new TransferParameters("2345678901234567", "02/23", "234", "3456789012345678", new Amount(100, "RUR"));
        transferParametersArray[2] = new TransferParameters("3456789012345678", "03/23", "345", "4567890123456789", new Amount(100, "RUR"));
        transferParametersArray[3] = new TransferParameters("4567890123456789", "04/23", "456", "1234567890123456", new Amount(100, "RUR"));

        return transferParametersArray;
    }

    @Test
    void test_service_requestTransfer_ok() {
        Map<String, Card> mapRepo = createMap();
        TransferParameters[] parametersArray = getTransportParametersArray_correctValues();

        for (int ii = 0; ii < parametersArray.length; ii++) {
            TransferParameters transferParameters = parametersArray[ii];

            Optional<Card> cardFromRepo = (mapRepo.containsKey(transferParameters.getCardFromNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardFromNumber())) : Optional.empty();
            when(cardRepository.getCard(transferParameters.getCardFromNumber()))
                    .thenReturn(cardFromRepo);

            Optional<Card> cardToRepo = (mapRepo.containsKey(transferParameters.getCardToNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardToNumber())) : Optional.empty();
            when(cardRepository.getCard(transferParameters.getCardToNumber()))
                    .thenReturn(cardToRepo);

            String operationId = transferService.requestTransfer(transferParameters);
            assertEquals(String.valueOf(ii + 1), operationId);

            Map<String, Transaction> actualOperationMap = transferService.getOperationMap();

            assertEquals(transferParameters.getCardFromNumber(), actualOperationMap.get(operationId).getCardFromNumber());
            assertEquals(transferParameters.getCardToNumber(), actualOperationMap.get(operationId).getCardToNumber());
            assertEquals(transferParameters.getAmount(), actualOperationMap.get(operationId).getAmount());
        }
    }

    // ==========================================================================================
    // Проверка правильной работы метода confirmOperation класса Service для корректных входных данных
    // и операций, которые могут быть выполнены

    public static TransferParameters[] getTransportParametersArray_correctValues_confirm() {
        TransferParameters[] transferParametersArray = new TransferParameters[7];

        transferParametersArray[0] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(100, "RUR"));
        transferParametersArray[1] = new TransferParameters("2345678901234567", "02/23", "234", "3456789012345678", new Amount(100, "RUR"));
        transferParametersArray[2] = new TransferParameters("3456789012345678", "03/23", "345", "4567890123456789", new Amount(100, "RUR"));
        transferParametersArray[3] = new TransferParameters("4567890123456789", "04/23", "456", "1234567890123456", new Amount(100, "RUR"));
        transferParametersArray[4] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(500, "RUR"));
        transferParametersArray[5] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(200, "RUR"));
        transferParametersArray[6] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(200, "RUR"));

        return transferParametersArray;
    }

    @Test
    void test_service_confirmOperation_ok() {
        Map<String, Card> mapRepo = createMap();
        TransferParameters[] parametersArray = getTransportParametersArray_correctValues_confirm();

        for (int ii = 0; ii < parametersArray.length; ii++) {
            TransferParameters transferParameters = parametersArray[ii];

            Optional<Card> cardFromRepo = (mapRepo.containsKey(transferParameters.getCardFromNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardFromNumber())) : Optional.empty();
            when(cardRepository.getCard(transferParameters.getCardFromNumber()))
                    .thenReturn(cardFromRepo);

            Optional<Card> cardToRepo = (mapRepo.containsKey(transferParameters.getCardToNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardToNumber())) : Optional.empty();
            when(cardRepository.getCard(transferParameters.getCardToNumber()))
                    .thenReturn(cardToRepo);

            String operationId = transferService.requestTransfer(transferParameters);

            int cardFromBalance = mapRepo.get(transferParameters.getCardFromNumber()).getBalance();
            int cardToBalance = mapRepo.get(transferParameters.getCardToNumber()).getBalance();

            when(cardRepository.offMoney(transferParameters.getCardFromNumber(), transferParameters.getAmount().getValue()))
                    .thenReturn(mapRepo.get(transferParameters.getCardFromNumber()).offMoney(transferParameters.getAmount().getValue()));

            when(cardRepository.chargeMoney(transferParameters.getCardToNumber(), transferParameters.getAmount().getValue()))
                    .thenReturn(mapRepo.get(transferParameters.getCardToNumber()).chargeMoney(transferParameters.getAmount().getValue()));


            ConfirmParameters confirmParameters = new ConfirmParameters(String.valueOf(ii + 1), "0000");
            transferService.confirmOperation(confirmParameters);

            assertFalse(transferService.getOperationMap().containsKey(String.valueOf(ii + 1)));
            assertEquals(cardFromBalance - transferParameters.getAmount().getValue(), mapRepo.get(transferParameters.getCardFromNumber()).getBalance());
            assertEquals(cardToBalance + transferParameters.getAmount().getValue(), mapRepo.get(transferParameters.getCardToNumber()).getBalance());
        }

        assertTrue(transferService.getOperationMap().isEmpty());
    }

    // ==========================================================================================
    // Проверка метода confirmOperation класса Service на выброс исключения
    // FailedOperationException в случае ввода несуществующего в карте операций operationId

    @Test
    void test_service_confirmOperation_FailedOperationException() {
        Map<String, Card> mapRepo = createMap();
        TransferParameters[] parametersArray = getTransportParametersArray_correctValues_confirm();

        for (int ii = 0; ii < parametersArray.length; ii++) {
            TransferParameters transferParameters = parametersArray[ii];

            Optional<Card> cardFromRepo = (mapRepo.containsKey(transferParameters.getCardFromNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardFromNumber())) : Optional.empty();
            when(cardRepository.getCard(transferParameters.getCardFromNumber()))
                    .thenReturn(cardFromRepo);

            Optional<Card> cardToRepo = (mapRepo.containsKey(transferParameters.getCardToNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardToNumber())) : Optional.empty();
            when(cardRepository.getCard(transferParameters.getCardToNumber()))
                    .thenReturn(cardToRepo);

            String operationId = transferService.requestTransfer(transferParameters);

            ConfirmParameters confirmParameters = new ConfirmParameters(String.valueOf(parametersArray.length + ii + 1), "0000");

            FailedOperationException e = assertThrows(FailedOperationException.class, () ->
                    transferService.confirmOperation(confirmParameters));

            String message = e.getMessage();
            assertTrue(message.equals("There is no operation with operationId = " + (parametersArray.length + ii + 1) + ". The operation could not be completed."));
        }

        assertTrue(transferService.getOperationMap().size() == parametersArray.length);
    }

    // ==========================================================================================
    // Проверка метода confirmOperation класса Service на выброс исключения
    // InsufficientFundsException в случае, когда баланс карты изменился, и суммы на карте уже
    // не достаточно для перевода

    public static TransferParameters[] getTransportParametersArray_incorrectAmount_confirm() {
        TransferParameters[] transferParametersArray = new TransferParameters[4];

        transferParametersArray[0] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(1000, "RUR"));
        transferParametersArray[1] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(1000, "RUR"));
        transferParametersArray[2] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(1000, "RUR"));
        transferParametersArray[3] = new TransferParameters("1234567890123456", "01/23", "123", "2345678901234567", new Amount(1000, "RUR"));


        return transferParametersArray;
    }

    @Test
    void test_service_confirmOperation_InsufficientFundsException() {
        Map<String, Card> mapRepo = createMap();
        TransferParameters[] parametersArray = getTransportParametersArray_incorrectAmount_confirm();

        for (int ii = 0; ii < parametersArray.length; ii++) {
            TransferParameters transferParameters = parametersArray[ii];

            Optional<Card> cardFromRepo = (mapRepo.containsKey(transferParameters.getCardFromNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardFromNumber())) : Optional.empty();
            when(cardRepository.getCard(transferParameters.getCardFromNumber()))
                    .thenReturn(cardFromRepo);

            Optional<Card> cardToRepo = (mapRepo.containsKey(transferParameters.getCardToNumber())) ? Optional.of(mapRepo.get(transferParameters.getCardToNumber())) : Optional.empty();
            when(cardRepository.getCard(transferParameters.getCardToNumber()))
                    .thenReturn(cardToRepo);

            String operationId = transferService.requestTransfer(transferParameters);
        }

        when(cardRepository.offMoney(parametersArray[0].getCardFromNumber(), parametersArray[0].getAmount().getValue()))
                .thenReturn(mapRepo.get(parametersArray[0].getCardFromNumber()).offMoney(parametersArray[0].getAmount().getValue()));

        when(cardRepository.chargeMoney(parametersArray[0].getCardToNumber(), parametersArray[0].getAmount().getValue()))
                .thenReturn(mapRepo.get(parametersArray[0].getCardToNumber()).chargeMoney(parametersArray[0].getAmount().getValue()));

        transferService.confirmOperation(new ConfirmParameters(String.valueOf(1), "0000"));

        for (int ii = 1; ii < parametersArray.length; ii++) {
            TransferParameters transferParameters = parametersArray[ii];

            when(cardRepository.offMoney(transferParameters.getCardFromNumber(), transferParameters.getAmount().getValue()))
                    .thenReturn(mapRepo.get(transferParameters.getCardFromNumber()).offMoney(transferParameters.getAmount().getValue()));

            ConfirmParameters confirmParameters = new ConfirmParameters(String.valueOf(ii + 1), "0000");

            InsufficientFundsException e = assertThrows(InsufficientFundsException.class, () ->
                    transferService.confirmOperation(confirmParameters));

            String message = e.getMessage();
            assertTrue(message.equals("there are not enough funds on the card " + transferParameters.getCardFromNumber() + " to complete operation with operationId = " + (ii + 1)));
        }

        assertTrue(transferService.getOperationMap().size() == (parametersArray.length - 1));
    }

}