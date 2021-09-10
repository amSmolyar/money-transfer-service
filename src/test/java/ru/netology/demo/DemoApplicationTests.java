package ru.netology.demo;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import ru.netology.demo.dto.Amount;
import ru.netology.demo.dto.ConfirmParameters;
import ru.netology.demo.dto.TransferParameters;
import ru.netology.demo.dto.ConfirmResponse;
import ru.netology.demo.dto.ExceptionResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {
    private static final int PORT = 5500;

    @Autowired
    TestRestTemplate restTemplate;

    private static GenericContainer<?> transferApp = new GenericContainer<>("transferapp")
            .withExposedPorts(PORT);

    @BeforeAll
    public static void setUp() {
        transferApp.start();
    }

    // Тест на корректный запрос-ответ на /transfer:

    @Test
    void transferRequest_ok() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        TransferParameters transferParameters = new TransferParameters("1234567890123456", "03/22", "123", "2345678901234567", new Amount(100, "RUR"));

        HttpEntity<TransferParameters> requestEntity = new HttpEntity<>(transferParameters, headers);

        String url = "http://localhost:" + transferApp.getMappedPort(PORT) + "/transfer";
        ResponseEntity<ConfirmResponse> postEntity = restTemplate.postForEntity(url, requestEntity, ConfirmResponse.class);
        ConfirmResponse response = postEntity.getBody();

        assertTrue(postEntity.getStatusCodeValue() == 200);
        assertTrue(new ConfirmResponse("3").equals(response) || new ConfirmResponse("1").equals(response));


        // 2-ой запрос на перевод

        transferParameters = new TransferParameters("2345678901234567", "04/23", "234", "3456789012345678", new Amount(100, "RUR"));
        requestEntity = new HttpEntity<>(transferParameters, headers);

        postEntity = restTemplate.postForEntity(url, requestEntity, ConfirmResponse.class);
        response = postEntity.getBody();

        assertTrue(postEntity.getStatusCodeValue() == 200);
        assertTrue(new ConfirmResponse("4").equals(response) || new ConfirmResponse("2").equals(response));


        // подтверждение:
        url = "http://localhost:" + transferApp.getMappedPort(PORT) + "/confirmOperation";

        ConfirmParameters confirmParameters = new ConfirmParameters("1", "0000");
        HttpEntity<ConfirmParameters> requestEntityConfirm = new HttpEntity<>(confirmParameters, headers);
        ResponseEntity<ConfirmResponse> postEntityConfirm = restTemplate.postForEntity(url, requestEntityConfirm, ConfirmResponse.class);

        // 2-ое подтверждение:

        confirmParameters = new ConfirmParameters("2", "0000");
        requestEntityConfirm = new HttpEntity<>(confirmParameters, headers);
        postEntityConfirm = restTemplate.postForEntity(url, requestEntityConfirm, ConfirmResponse.class);

    }

    // Тест на возврат 400ой ошибки в ответ на запрос с неверным форматом данных (/transfer):

    @Test
    void transferRequest_badDataInputFormat() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        TransferParameters transferParameters = new TransferParameters("1", "03/22", "123", "2345678901234567", new Amount(100, "RUR"));
        HttpEntity<TransferParameters> requestEntity = new HttpEntity<>(transferParameters, headers);

        String url = "http://localhost:" + transferApp.getMappedPort(PORT) + "/transfer";
        ResponseEntity<ConfirmResponse> postEntity = restTemplate.postForEntity(url, requestEntity, ConfirmResponse.class);

        assertTrue(postEntity.getStatusCodeValue() == 400);

        // 2-ой запрос на перевод (срок действия)

        transferParameters = new TransferParameters("2345678901234567", "0423", "234", "3456789012345678", new Amount(100, "RUR"));
        requestEntity = new HttpEntity<>(transferParameters, headers);
        postEntity = restTemplate.postForEntity(url, requestEntity, ConfirmResponse.class);
        assertTrue(postEntity.getStatusCodeValue() == 400);

        // 3-ий запрос на перевод (cvv)

        transferParameters = new TransferParameters("2345678901234567", "04/23", "2345", "3456789012345678", new Amount(100, "RUR"));
        requestEntity = new HttpEntity<>(transferParameters, headers);
        postEntity = restTemplate.postForEntity(url, requestEntity, ConfirmResponse.class);
        assertTrue(postEntity.getStatusCodeValue() == 400);

        // 4-ый запрос на перевод (номер карты получателя)

        transferParameters = new TransferParameters("2345678901234567", "04/23", "234", "3456789012", new Amount(100, "RUR"));
        requestEntity = new HttpEntity<>(transferParameters, headers);
        postEntity = restTemplate.postForEntity(url, requestEntity, ConfirmResponse.class);
        assertTrue(postEntity.getStatusCodeValue() == 400);

        // 5-ый запрос на перевод (валюта)

        transferParameters = new TransferParameters("2345678901234567", "04/23", "234", "3456789012345678", new Amount(100, "RU"));
        requestEntity = new HttpEntity<>(transferParameters, headers);
        postEntity = restTemplate.postForEntity(url, requestEntity, ConfirmResponse.class);
        assertTrue(postEntity.getStatusCodeValue() == 400);

        // 6-ой запрос на перевод (сумма)

        transferParameters = new TransferParameters("2345678901234567", "04/23", "234", "3456789012345678", new Amount(-100, "RUR"));
        requestEntity = new HttpEntity<>(transferParameters, headers);
        postEntity = restTemplate.postForEntity(url, requestEntity, ConfirmResponse.class);
        assertTrue(postEntity.getStatusCodeValue() == 400);
    }

    // Запросы с выбросом исключений:

    @Test
    void transferRequest_BadRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // cardFromNumber нет в базе
        TransferParameters transferParameters = new TransferParameters("3214567890123456", "03/22", "123", "2345678901234567", new Amount(100, "RUR"));
        HttpEntity<TransferParameters> requestEntity = new HttpEntity<>(transferParameters, headers);

        String url = "http://localhost:" + transferApp.getMappedPort(PORT) + "/transfer";
        ResponseEntity<ExceptionResponse> postEntity = restTemplate.postForEntity(url, requestEntity, ExceptionResponse.class);
        ExceptionResponse response = postEntity.getBody();
        String message = response.getMessage();
        int id = response.getId();

        assertTrue(postEntity.getStatusCodeValue() == 400);
        assertEquals("there is no card with number " + transferParameters.getCardFromNumber() + " in the database", message);
        assertEquals(1, id);

        // 2-ой запрос на перевод (срок действия не совпадает)

        transferParameters = new TransferParameters("1234567890123456", "03/25", "123", "2345678901234567", new Amount(100, "RUR"));
        requestEntity = new HttpEntity<>(transferParameters, headers);
        postEntity = restTemplate.postForEntity(url, requestEntity, ExceptionResponse.class);
        response = postEntity.getBody();
        message = response.getMessage();
        id = response.getId();

        assertTrue(postEntity.getStatusCodeValue() == 400);
        assertEquals("the card " + transferParameters.getCardFromNumber() + " expiration date is incorrect", message);
        assertEquals(1, id);

        // 3-ий запрос на перевод (cvv не совпадает)

        transferParameters = new TransferParameters("1234567890123456", "03/22", "125", "3456789012345678", new Amount(100, "RUR"));
        requestEntity = new HttpEntity<>(transferParameters, headers);
        postEntity = restTemplate.postForEntity(url, requestEntity, ExceptionResponse.class);
        response = postEntity.getBody();
        message = response.getMessage();
        id = response.getId();

        assertTrue(postEntity.getStatusCodeValue() == 400);
        assertEquals("the card " + transferParameters.getCardFromNumber() + " cvv code is incorrect", message);
        assertEquals(1, id);

        // 4-ый запрос на перевод (номера карты получателя нет в базе)

        transferParameters = new TransferParameters("1234567890123456", "03/22", "123", "0864213579086424", new Amount(100, "RUR"));
        requestEntity = new HttpEntity<>(transferParameters, headers);
        postEntity = restTemplate.postForEntity(url, requestEntity, ExceptionResponse.class);
        response = postEntity.getBody();
        message = response.getMessage();
        id = response.getId();

        assertTrue(postEntity.getStatusCodeValue() == 400);
        assertEquals("there is no card with number " + transferParameters.getCardToNumber() + " in the database", message);
        assertEquals(1, id);

        // 5-ый запрос на перевод (валюта)

        transferParameters = new TransferParameters("1234567890123456", "03/22", "123", "2345678901234567", new Amount(100000, "RUR"));
        requestEntity = new HttpEntity<>(transferParameters, headers);
        postEntity = restTemplate.postForEntity(url, requestEntity, ExceptionResponse.class);
        response = postEntity.getBody();
        message = response.getMessage();
        id = response.getId();

        assertTrue(postEntity.getStatusCodeValue() == 400);
        assertEquals("there are not enough funds on the card " + transferParameters.getCardFromNumber() + " for transfer", message);
        assertEquals(4, id);
    }

    // Тест на корректный запрос-ответ на /confirmOperation:

    @Test
    void confirmRequest_ok() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "http://localhost:" + transferApp.getMappedPort(PORT) + "/transfer";

        TransferParameters transferParameters = new TransferParameters("1234567890123456", "03/22", "123", "2345678901234567", new Amount(100, "RUR"));
        HttpEntity<TransferParameters> requestEntity = new HttpEntity<>(transferParameters, headers);
        ResponseEntity<ConfirmResponse> postEntity = restTemplate.postForEntity(url, requestEntity, ConfirmResponse.class);

        // 2-ой запрос на перевод

        transferParameters = new TransferParameters("2345678901234567", "04/23", "234", "3456789012345678", new Amount(100, "RUR"));
        requestEntity = new HttpEntity<>(transferParameters, headers);
        postEntity = restTemplate.postForEntity(url, requestEntity, ConfirmResponse.class);

        // подтверждение:
        url = "http://localhost:" + transferApp.getMappedPort(PORT) + "/confirmOperation";

        ConfirmParameters confirmParameters = new ConfirmParameters("1", "0000");
        HttpEntity<ConfirmParameters> requestEntityConfirm = new HttpEntity<>(confirmParameters, headers);
        ResponseEntity<ConfirmResponse> postEntityConfirm = restTemplate.postForEntity(url, requestEntityConfirm, ConfirmResponse.class);

        ConfirmResponse response = postEntityConfirm.getBody();

        assertTrue(postEntity.getStatusCodeValue() == 200);
        assertTrue(new ConfirmResponse("1").equals(response));

        // 2-ое подтверждение:

        confirmParameters = new ConfirmParameters("2", "0000");
        requestEntityConfirm = new HttpEntity<>(confirmParameters, headers);
        postEntityConfirm = restTemplate.postForEntity(url, requestEntityConfirm, ConfirmResponse.class);

        response = postEntityConfirm.getBody();

        assertTrue(postEntity.getStatusCodeValue() == 200);
        assertTrue(new ConfirmResponse("2").equals(response));
    }

    // Тест на возврат 400ой ошибки в ответ на запрос с неверным форматом данных (/confirmOperation):

    @Test
    void confirmRequest_badDataInputFormat() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "http://localhost:" + transferApp.getMappedPort(PORT) + "/confirmOperation";

        ConfirmParameters confirmParameters = new ConfirmParameters("one", "0000");
        HttpEntity<ConfirmParameters> requestEntityConfirm = new HttpEntity<>(confirmParameters, headers);
        ResponseEntity<ConfirmResponse> postEntityConfirm = restTemplate.postForEntity(url, requestEntityConfirm, ConfirmResponse.class);

        assertTrue(postEntityConfirm.getStatusCodeValue() == 400);

        // 2-ой запрос на перевод (срок действия)

        confirmParameters = new ConfirmParameters("1", "code");
        requestEntityConfirm = new HttpEntity<>(confirmParameters, headers);
        postEntityConfirm = restTemplate.postForEntity(url, requestEntityConfirm, ConfirmResponse.class);

        assertTrue(postEntityConfirm.getStatusCodeValue() == 400);

    }

    @Test
    void confirmRequest_badRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "http://localhost:" + transferApp.getMappedPort(PORT) + "/confirmOperation";

        ConfirmParameters confirmParameters = new ConfirmParameters("100", "0000");
        HttpEntity<ConfirmParameters> requestEntityConfirm = new HttpEntity<>(confirmParameters, headers);
        ResponseEntity<ExceptionResponse> postEntityConfirm = restTemplate.postForEntity(url, requestEntityConfirm, ExceptionResponse.class);
        ExceptionResponse response = postEntityConfirm.getBody();
        String message = response.getMessage();
        int id = response.getId();

        assertTrue(postEntityConfirm.getStatusCodeValue() == 400);
        assertEquals("There is no operation with operationId = " + confirmParameters.getOperationId() + ". The operation could not be completed.", message);
        assertEquals(3, id);

        // 2-ой запрос на перевод (срок действия)

        confirmParameters = new ConfirmParameters("2000", "0000");
        requestEntityConfirm = new HttpEntity<>(confirmParameters, headers);
        postEntityConfirm = restTemplate.postForEntity(url, requestEntityConfirm, ExceptionResponse.class);
        response = postEntityConfirm.getBody();
        message = response.getMessage();
        id = response.getId();

        assertTrue(postEntityConfirm.getStatusCodeValue() == 400);
        assertEquals("There is no operation with operationId = " + confirmParameters.getOperationId() + ". The operation could not be completed.", message);
        assertEquals(3, id);
    }

}
