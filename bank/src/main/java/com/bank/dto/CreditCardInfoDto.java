package com.bank.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreditCardInfoDto {

    @CreditCardNumber(message="Not a valid credit card number")
    private String pan;

    @Digits(integer=3, fraction=0, message="Invalid CVV")
    private String cvv;

    private String cardholderName;

    //@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
    private LocalDate expirationDate;

}
