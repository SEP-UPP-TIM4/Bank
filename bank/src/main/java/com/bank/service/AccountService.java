package com.bank.service;

import com.bank.exception.BadCredentialsException;
import com.bank.exception.NotEnoughFundsException;
import com.bank.exception.NotFoundException;
import com.bank.model.Account;
import com.bank.model.Payment;
import com.bank.model.TransactionStatus;
import com.bank.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final TransactionService transactionService;

    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, TransactionService transactionService, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateAccount(UUID merchantId, String merchantPassword) {
        Account account = accountRepository.findById(merchantId).orElseThrow(() ->new NotFoundException(Account.class.getSimpleName()));
        if(!passwordEncoder.matches(merchantPassword,account.getPassword())) throw new BadCredentialsException(merchantId.toString());
    }

    public Account pay(Account issuer, Payment payment) throws NotEnoughFundsException{
        int compareResult = issuer.getAmount().compareTo(payment.getAcquirerTransaction().getAmount());
        if(compareResult < 0){
            payment.getAcquirerTransaction().setStatus(TransactionStatus.FAILED);
            transactionService.save(payment.getAcquirerTransaction());
            throw new NotEnoughFundsException(payment.getFailedUrl());
        }
        Account acquirer = payment.getAcquirerTransaction().getAccount();
        acquirer.setAmount(acquirer.getAmount().add(payment.getAcquirerTransaction().getAmount()));
        accountRepository.save(acquirer);
        payment.getAcquirerTransaction().setStatus(TransactionStatus.PROCESSED);
        issuer.setAmount(issuer.getAmount().subtract(payment.getAcquirerTransaction().getAmount()));
        return accountRepository.save(issuer);
    }

    public Account getById(UUID merchantId) {
        return accountRepository.findById(merchantId).orElseThrow(() ->new NotFoundException(Account.class.getSimpleName()));
    }
}
