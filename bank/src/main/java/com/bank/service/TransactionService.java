package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Currency;
import com.bank.model.Transaction;
import com.bank.model.Status;
import com.bank.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public Transaction createAcquirerTransaction(Account account, BigDecimal amount, String currency) {
        Transaction transaction = createNewTransaction(account, amount, Enum.valueOf(Currency.class, currency));
        transaction.setIncome(true);
        return transactionRepository.save(transaction);
    }

    public Transaction createIssuerTransaction(Account issuer, BigDecimal amount, Currency currency) {
        accountService.decreaseAmount(issuer, amount);
        Transaction transaction = createNewTransaction(issuer, amount, currency);
        transaction.setIncome(false);
        transaction.setStatus(Status.PROCESSED);
        return transactionRepository.save(transaction);
    }

    private Transaction createNewTransaction(Account account, BigDecimal amount, Currency currency) {
        return Transaction.builder().account(account).amount(amount)
                .currency(currency).build();
    }
}
