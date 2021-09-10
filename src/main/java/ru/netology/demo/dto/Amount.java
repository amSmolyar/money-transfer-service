package ru.netology.demo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Objects;

public class Amount {
    @Min(0)
    private int value;

    @Pattern(regexp = "RUR")
    private String currency;

    @JsonCreator
    public Amount(int value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public int getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return value == amount.value &&
                currency.equals(amount.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, currency);
    }
}
