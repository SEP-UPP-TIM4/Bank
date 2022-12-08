package com.bank.service;

import com.bank.dto.ValidateRequestDto;
import com.bank.model.Currency;
import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createNewTransaction(UUID acquirerId, BigDecimal amount, String currency) {
        Transaction transaction = Transaction.builder().acquirerId(acquirerId).amount(amount)
                .currency(Enum.valueOf(Currency.class, currency)).build();
        return transactionRepository.save(transaction);
    }
}
