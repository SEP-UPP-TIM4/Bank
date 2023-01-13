package com.bank.service;

import com.bank.exception.BadCredentialsException;
import com.bank.exception.NotEnoughFundsException;
import com.bank.exception.NotFoundException;
import com.bank.model.*;
import com.bank.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateAccount(UUID merchantId, String merchantPassword) {
        Account account = accountRepository.findById(merchantId).orElseThrow(() ->new NotFoundException(Account.class.getSimpleName()));
        if(!passwordEncoder.matches(merchantPassword,account.getPassword())) throw new BadCredentialsException(merchantId.toString());
    }

    // TODO: remove this method - remove call from QRCodeService
    public Account processInternalPayment(Account issuer, Payment payment) throws NotEnoughFundsException{
        int compareResult = issuer.getAmount().compareTo(payment.getAcquirerTransaction().getAmount());
        if(compareResult < 0){
            throw new NotEnoughFundsException(payment.getFailedUrl());
        }
        Account acquirer = payment.getAcquirerTransaction().getAccount();
        acquirer.setAmount(acquirer.getAmount().add(payment.getAcquirerTransaction().getAmount()));
        accountRepository.save(acquirer);
        payment.getAcquirerTransaction().setStatus(Status.PROCESSED);
        issuer.setAmount(issuer.getAmount().subtract(payment.getAcquirerTransaction().getAmount()));
        return accountRepository.save(issuer);
    }

    public void increaseAmount(Account account, BigDecimal amount) {
        account.setAmount(account.getAmount().add(amount));
        accountRepository.save(account);
    }

    public void decreaseAmount(Account issuer, BigDecimal amount) {
        issuer.setAmount(issuer.getAmount().subtract(amount));
        accountRepository.save(issuer);
    }

    public Account getById(UUID merchantId) {
        return accountRepository.findById(merchantId).orElseThrow(() ->new NotFoundException(Account.class.getSimpleName()));
    }

    public void makePayment(BigDecimal amount, Currency currency, Account issuer) {
        int result = issuer.getAmount().compareTo(amount);
        if(result < 0) {

            return;
        }
        Transaction issuerTransaction = Transaction.builder()
                .amount(amount)
                .currency(currency)
                .status(Status.PROCESSED)
                .account(issuer)
                .income(false)
                .build();
    }
}
