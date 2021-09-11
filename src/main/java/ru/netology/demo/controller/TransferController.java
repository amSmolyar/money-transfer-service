package ru.netology.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netology.demo.dto.ConfirmParameters;
import ru.netology.demo.dto.TransferParameters;
import ru.netology.demo.dto.ConfirmResponse;
import ru.netology.demo.service.TransferService;

import javax.validation.Valid;


@RestController
@Validated
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @CrossOrigin
    @PostMapping("/transfer")
    public ResponseEntity<ConfirmResponse> requestTransfer(@Valid @RequestBody TransferParameters parameters) {
        LoggerController.getLogger().info("Client wants to transfer " + parameters.toString());
        ConfirmResponse operationId = new ConfirmResponse(transferService.requestTransfer(parameters));
        LoggerController.getLogger().info("Confirmation is required for transfer " + parameters.toString() + ". OperationId = " + operationId.getOperationId());

        return ResponseEntity.ok(operationId);
    }

    @CrossOrigin
    @PostMapping("/confirmOperation")
    public ResponseEntity<ConfirmResponse> confirmTransfer(@Valid @RequestBody ConfirmParameters parameters) {
        LoggerController.getLogger().info("Received confirmation of transfer for " + parameters.toString());
        ConfirmResponse operationId = new ConfirmResponse(transferService.confirmOperation(parameters));
        return ResponseEntity.ok(operationId);
    }

}
