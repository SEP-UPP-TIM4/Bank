package com.bank.service;

import com.bank.dto.CreditCardInfoDto;
import com.bank.model.CreditCard;
import com.bank.repository.CreditCardRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;

    public CreditCardService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public Optional<CreditCard> getCreditCard(String pan){
        return creditCardRepository.findByPan(pan);
    }

}
