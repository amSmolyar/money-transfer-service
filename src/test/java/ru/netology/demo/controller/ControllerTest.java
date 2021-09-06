package ru.netology.demo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.junit.platform.runner.JUnitPlatform;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.netology.demo.requestObjects.Amount;
import ru.netology.demo.requestObjects.ConfirmParameters;
import ru.netology.demo.requestObjects.TransferParameters;
import ru.netology.demo.responseObjects.ConfirmResponse;
import ru.netology.demo.service.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class ControllerTest_junit {

    @InjectMocks
    Controller controller;

    @Mock
    Service service;

    @ParameterizedTest
    @ValueSource(strings = {"1", "20", "300", "4000", "50000"})
    void test_requestTransfer(String id) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Amount amount = new Amount(100, "RUR");
        TransferParameters transferParameters = new TransferParameters("1234567890123456", "03/22", "123", "2345678901234567", amount);

        when(service.requestTransfer(any(TransferParameters.class))).thenReturn(id);

        ResponseEntity<ConfirmResponse> responseEntity = controller.requestTransfer(transferParameters);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody().getOperationId()).isEqualTo(id);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "20", "300", "4000", "50000", "hello"})
    void test_confirmTransfer(String id) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ConfirmParameters confirmParameters = new ConfirmParameters(id, "0000");

        when(service.confirmOperation(any(ConfirmParameters.class))).thenReturn(id);

        ResponseEntity<ConfirmResponse> responseEntity = controller.confirmTransfer(confirmParameters);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody().getOperationId()).isEqualTo(id);

    }
}