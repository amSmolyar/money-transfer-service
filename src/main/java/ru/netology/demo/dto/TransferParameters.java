package ru.netology.demo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@Validated
public class TransferParameters {
    @Pattern(regexp = "(\\d{16})")
    private final String cardFromNumber;

    @Pattern(regexp = "(\\d{2})(\\/)(\\d{2})")
    private final String cardFromValidTill;

    @Pattern(regexp = "(\\d{3})")
    private final String cardFromCVV;

    @Pattern(regexp = "(\\d{16})")
    private final String cardToNumber;

    @Valid
    private final Amount amount;

    @JsonCreator
    public @Valid TransferParameters(String cardFromNumber, String cardFromValidTill, String cardFromCVV, String cardToNumber, Amount amount) {
        this.cardFromNumber = cardFromNumber;
        this.cardFromValidTill = cardFromValidTill;
        this.cardFromCVV = cardFromCVV;
        this.cardToNumber = cardToNumber;
        this.amount = amount;
    }

    public String getCardFromNumber() {
        return cardFromNumber;
    }

    public String getCardFromValidTill() {
        return cardFromValidTill;
    }

    public String getCardFromCVV() {
        return cardFromCVV;
    }

    public String getCardToNumber() {
        return cardToNumber;
    }

    public Amount getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return amount.getValue() + " " + amount.getCurrency() +
                " from card {" + cardFromNumber + ", " +
                cardFromValidTill + ", " + cardFromCVV + "}" +
                " to card number " + cardToNumber;
    }
}
