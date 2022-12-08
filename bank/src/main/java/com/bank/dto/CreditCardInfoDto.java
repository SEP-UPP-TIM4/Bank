package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreditCardInfoDto {
    private String pan;
    private String cvv;
    private String cardHolderName;
    private LocalDate expirationDate;
}
