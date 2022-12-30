package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewQrCodeDto {

    //@NotBlank(message = "Identification code is required")
    //@IdentificationCodeConstraint
    private String k;//identifikacija
    //@NotBlank(message = "Version is required")
    //@VersionConstraint
    private String v; //verzija
    //@NotBlank(message = "Character set is required")
    //@CharacterSetConstraint
    private String c; //znakovni skup
    //@NotBlank(message = "Payee account number is required")
    //@PayeeAccountConstraint
    private String r; //broj racuna mora po sablonu br cifara iz 3 dijel
    //@NotBlank(message = "Payee personal data is required")
    //@PayeePersonalDataLengthConstraint
    //@PayeePersonalDataNumberOfRowsConstraint
    private String n; //naziv primaoca placanja
    //@NotBlank(message = "Payment amount is required")
    //@PaymentAmountConstraint
    private String i;//rsd1232,13
    //@PayerPersonalDataLengthConstraint
    //@PayerPersonalDataNumberOfRowsConstraint
    private String p;//podaci o platiocu
    //@NotBlank(message = "Payment code is required")
    //@PaymentCodeConstraint
    private String sf;//kod placanja mozda
    //@PaymentPurposeConstraint
    private String s;//svrha placanja
    //@ModelAndReferenceNumberConstraint
    private String ro;//poziv na br

}
