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

    public Account pay(Account account, Payment payment) throws NotEnoughFundsException{
        int compareResult = account.getAmount().compareTo(payment.getTransaction().getAmount());
        if(compareResult < 0){
            payment.getTransaction().setStatus(TransactionStatus.FAILED);
            payment.getTransaction().setIssuer(account);
            transactionService.save(payment.getTransaction());
            throw new NotEnoughFundsException(payment.getFailedUrl());
        }
        payment.getTransaction().setStatus(TransactionStatus.PROCESSED);
        account.setAmount(account.getAmount().subtract(payment.getTransaction().getAmount()));
        account.setReservedAmount(account.getReservedAmount().add(payment.getTransaction().getAmount()));
        return accountRepository.save(account);
    }

    public Account getById(UUID merchantId) {
        return accountRepository.findById(merchantId).orElseThrow(() ->new NotFoundException(Account.class.getSimpleName()));
    }
}
