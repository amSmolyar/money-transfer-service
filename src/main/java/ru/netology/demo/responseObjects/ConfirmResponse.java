package ru.netology.demo.responseObjects;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ConfirmResponse {
    private String operationId;

    @JsonCreator
    public ConfirmResponse(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationId() {
        return operationId;
    }
}
