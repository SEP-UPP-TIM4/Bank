package com.bank.service;

import com.bank.dto.CreditCardInfoDto;
import com.bank.exception.BadCvvException;
import com.bank.exception.InvalidExpirationDateException;
import com.bank.exception.NotFoundException;
import com.bank.exception.WrongCardholderNameException;
import com.bank.model.CreditCard;
import com.bank.model.Payment;
import com.bank.repository.CreditCardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;

    public CreditCardService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public Optional<CreditCard> getCreditCard(CreditCardInfoDto creditCardInfo){
        Optional<CreditCard> creditCard = creditCardRepository.findByPan(creditCardInfo.getPan());
        if(creditCard.isEmpty()) return Optional.empty();
        if(checkCardholderName(creditCardInfo.getCardholderName(), creditCard.get().getAccount().getName())) {
            throw new WrongCardholderNameException();
        }
        if(checkExpirationDate(creditCardInfo.getExpirationDate(), creditCard.get().getExpirationDate())) {
            throw new InvalidExpirationDateException();
        }
        if(validateSecurityCode(creditCardInfo.getCvv(), creditCard.get().getCvv())) {
            throw new BadCvvException();
        }
        return creditCard;
    }

    private boolean checkCardholderName(String cardholderName, String expectedCardholderName) {
        return !cardholderName.equals(expectedCardholderName);
    }

    private boolean checkExpirationDate(LocalDate date, LocalDate expectedDate) {
        //return !date.getMonth().equals(expectedDate.getMonth()) || !(date.getYear() == expectedDate.getYear()) || date.isBefore(LocalDate.now());
        return false;
    }

    public CreditCard findByAccountId(UUID id){return creditCardRepository.findByAccountId(id).orElseThrow(() -> new NotFoundException(CreditCard.class.getSimpleName()));}

    private boolean validateSecurityCode(String cvv, String expectedCvv) {
        return !cvv.equals(expectedCvv);
    }
}
