package com.bank.service;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.NotEnoughFundsException;
import com.bank.model.Account;
import com.bank.model.Payment;
import com.bank.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void validateAccount(UUID merchantId, String merchantPassword) throws AccountNotFoundException {
        Optional<Account> account = accountRepository.findById(merchantId);
        if(account.isEmpty() || !passwordEncoder.matches(merchantPassword,account.get().getPassword()))
            throw new AccountNotFoundException(merchantId.toString());
    }

    public Account pay(Account account, Payment payment){
        int compareResult = account.getAmount().compareTo(payment.getTransaction().getAmount());
        if(compareResult == -1){
            throw new NotEnoughFundsException();
        }
        account.setAmount(account.getAmount().subtract(payment.getTransaction().getAmount()));
        account.setReservedAmount(account.getReservedAmount().add(payment.getTransaction().getAmount()));
        return accountRepository.save(account);
    }
}
