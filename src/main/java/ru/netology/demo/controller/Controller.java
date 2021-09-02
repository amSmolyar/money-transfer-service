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
        ConfirmResponse operationId = new ConfirmResponse(service.requestTransfer(parameters));
        return ResponseEntity.ok(operationId);
    }

    @CrossOrigin
    @PostMapping("/confirmOperation")
    public ResponseEntity<ConfirmResponse> confirmTransfer(@RequestBody ConfirmParameters parameters) {
        ConfirmResponse operationId = new ConfirmResponse(service.confirmOperation(parameters));
        return ResponseEntity.ok(operationId);
    }

}
