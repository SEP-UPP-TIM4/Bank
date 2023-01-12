package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Currency;
import com.bank.model.Transaction;
import com.bank.model.TransactionStatus;
import com.bank.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class  TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createAcquirerTransaction(Account account, BigDecimal amount, String currency) {
        Transaction transaction = createNewTransaction(account, amount, Enum.valueOf(Currency.class, currency));
        transaction.setIncome(true);
        return transactionRepository.save(transaction);
    }

    public Transaction createIssuerTransaction(Account account, BigDecimal amount, Currency currency) {
        Transaction transaction = createNewTransaction(account, amount, currency);
        transaction.setIncome(false);
        return transactionRepository.save(transaction);
    }

    private Transaction createNewTransaction(Account account, BigDecimal amount, Currency currency) {
        return Transaction.builder().account(account).amount(amount)
                .currency(currency).build();
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
