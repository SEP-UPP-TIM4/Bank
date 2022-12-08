package com.bank.service;

import com.bank.dto.PaymentRequestDto;
import com.bank.model.Currency;
import com.bank.model.Transaction;
import com.bank.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createNewTransaction(PaymentRequestDto requestDto) {
        Transaction transaction = Transaction.builder().acquirerId(requestDto.getMerchantId()).amount(requestDto.getAmount())
                .currency(Enum.valueOf(Currency.class, requestDto.getCurrency()))
                .successUrl(requestDto.getSuccessUrl()).failedUrl(requestDto.getFailedUrl()).errorUrl(requestDto.getErrorUrl()).build();
        return transactionRepository.save(transaction);
    }
}
