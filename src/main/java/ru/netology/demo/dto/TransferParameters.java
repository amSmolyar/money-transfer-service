package ru.netology.demo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.validation.annotation.Validated;
import ru.netology.demo.dto.Amount;

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
