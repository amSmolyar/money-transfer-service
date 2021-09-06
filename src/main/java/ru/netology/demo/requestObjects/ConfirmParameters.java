package ru.netology.demo.requestObjects;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

public class ConfirmParameters {
    @Pattern(regexp = "(\\d+)")
    private String operationId;

    @Pattern(regexp = "(\\d+)")
    private String code;

    public @Valid  ConfirmParameters(String operationId, String code) {

        this.operationId = operationId;
        this.code = code;
    }

    public String getOperationId() {
        return operationId;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return  "operationId = " + operationId + ", " +
                "confirmation code = " + code;
    }
}
