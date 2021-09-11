package ru.netology.demo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.Pattern;
import java.util.Objects;

public class ConfirmResponse {
    @Pattern(regexp = "(\\d+)")
    private final String operationId;

    @JsonCreator
    public ConfirmResponse(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationId() {
        return operationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmResponse that = (ConfirmResponse) o;
        return operationId.equals(that.operationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId);
    }
}
