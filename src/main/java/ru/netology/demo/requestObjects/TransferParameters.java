package ru.netology.demo.requestObjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@Validated
public class TransferParameters {
    @Pattern(regexp = "(\\d{16})")
    private String cardFromNumber;

    @Pattern(regexp = "(\\d{2})(\\/)(\\d{2})")
    private String cardFromValidTill;

    @Pattern(regexp = "(\\d{3})")
    private String cardFromCVV;

    @Pattern(regexp = "(\\d{16})")
    private String cardToNumber;

    @Valid
    private Amount amount;

    @JsonCreator
    public TransferParameters(String cardFromNumber, String cardFromValidTill, String cardFromCVV, String cardToNumber, @Valid Amount amount) {
        this.cardFromNumber = cardFromNumber;
        this.cardFromValidTill = cardFromValidTill;
        this.cardFromCVV = cardFromCVV;
        this.cardToNumber = cardToNumber;
        this.amount = amount;
    }

    public String getCardFromNumber() {
        return cardFromNumber;
    }

    public void setCardFromNumber(String cardFromNumber) {
        this.cardFromNumber = cardFromNumber;
    }

    public String getCardFromValidTill() {
        return cardFromValidTill;
    }

    public void setCardFromValidTill(String cardFromValidTill) {
        this.cardFromValidTill = cardFromValidTill;
    }

    public String getCardFromCVV() {
        return cardFromCVV;
    }

    public void setCardFromCVV(String cardFromCVV) {
        this.cardFromCVV = cardFromCVV;
    }

    public String getCardToNumber() {
        return cardToNumber;
    }

    public void setCardToNumber(String cardToNumber) {
        this.cardToNumber = cardToNumber;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return amount.getValue() + " " + amount.getCurrency() +
                " from card {" + cardFromNumber + ", " +
                cardFromValidTill + ", " + cardFromCVV + "}" +
                " to card number " + cardToNumber;
    }
}
