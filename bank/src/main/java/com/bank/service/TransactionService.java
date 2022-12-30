package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Currency;
import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class  TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createNewTransaction(Account acquirer, BigDecimal amount, String currency) {
        Transaction transaction = Transaction.builder().acquirer(acquirer).amount(amount)
                .currency(Enum.valueOf(Currency.class, currency)).build();
        return transactionRepository.save(transaction);
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
