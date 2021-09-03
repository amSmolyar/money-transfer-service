package ru.netology.demo.requestObjects;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

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

    public void setValue(int value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
