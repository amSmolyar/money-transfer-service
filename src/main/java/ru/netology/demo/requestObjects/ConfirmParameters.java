package ru.netology.demo.requestObjects;

import javax.validation.constraints.Pattern;

public class ConfirmParameters {
    @Pattern(regexp = "(\\d+)")
    private String operationId;

    @Pattern(regexp = "(\\d+)")
    private String code;

    public ConfirmParameters(String operationId, String code) {
        this.operationId = operationId;
        this.code = code;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return  "operationId = " + operationId + ", " +
                "confirmation code = " + code;
    }
}
