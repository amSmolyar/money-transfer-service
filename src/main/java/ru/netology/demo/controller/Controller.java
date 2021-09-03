package ru.netology.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netology.demo.requestObjects.ConfirmParameters;
import ru.netology.demo.requestObjects.TransferParameters;
import ru.netology.demo.responseObjects.ConfirmResponse;
import ru.netology.demo.service.Service;


@RestController
@Validated
public class Controller {
    private Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @CrossOrigin
    @PostMapping("/transfer")
    public ResponseEntity<ConfirmResponse> requestTransfer(@RequestBody TransferParameters parameters) {
        LoggerController.getLogger().info("Client wants to transfer " + parameters.toString());
        ConfirmResponse operationId = new ConfirmResponse(service.requestTransfer(parameters));
        LoggerController.getLogger().info("Confirmation is required for transfer " + parameters.toString() + ". OperationId = " + operationId.getOperationId());
        return ResponseEntity.ok(operationId);
    }

    @CrossOrigin
    @PostMapping("/confirmOperation")
    public ResponseEntity<ConfirmResponse> confirmTransfer(@RequestBody ConfirmParameters parameters) {
        LoggerController.getLogger().info("Received confirmation of transfer for " + parameters.toString());
        ConfirmResponse operationId = new ConfirmResponse(service.confirmOperation(parameters));
        return ResponseEntity.ok(operationId);
    }

}
