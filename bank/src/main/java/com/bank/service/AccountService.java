package com.bank.service;

import com.bank.dto.PaymentRequestDto;
import com.bank.dto.PaymentResponseDto;
import com.bank.exception.AccountNotFoundException;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final TransactionService transactionService;

    private final String PAYMENT_URL = "localhost:4201/payment";

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionService = transactionService;
    }

    public PaymentResponseDto getPaymentUrlAndId(PaymentRequestDto requestDto) throws AccountNotFoundException {
        validateAccount(requestDto.getMerchantId(), requestDto.getMerchantPassword());
        Transaction newTransaction = transactionService.createNewTransaction(requestDto);
        return new PaymentResponseDto(PAYMENT_URL, newTransaction.getId());
    }

    private void validateAccount(UUID merchantId, String merchantPassword) throws AccountNotFoundException {
        Optional<Account> account = accountRepository.findById(merchantId);
        if(account.isEmpty() || !passwordEncoder.matches(merchantPassword,account.get().getPassword()))
            throw new AccountNotFoundException(merchantId.toString());
    }
}
