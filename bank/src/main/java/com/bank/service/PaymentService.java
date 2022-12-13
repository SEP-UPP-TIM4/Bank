package com.bank.service;

import com.bank.dto.CreditCardInfoDto;
import com.bank.dto.ValidateRequestDto;
import com.bank.dto.ValidateResponseDto;
import com.bank.exception.ErrorInCommunicationException;
import com.bank.exception.NotFoundException;
import com.bank.model.Account;
import com.bank.model.CreditCard;
import com.bank.model.Payment;
import com.bank.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private final TransactionService transactionService;
    private final PaymentRepository paymentRepository;
    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final String PAYMENT_URL = "http://localhost:4201/payment";

    public PaymentService(TransactionService transactionService, PaymentRepository paymentRepository,
                          AccountService accountService, CreditCardService creditCardService) {
        this.transactionService = transactionService;
        this.paymentRepository = paymentRepository;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
    }

    public ValidateResponseDto getPaymentUrlAndId(ValidateRequestDto requestDto) {
        accountService.validateAccount(UUID.fromString(requestDto.getMerchantId()), requestDto.getMerchantPassword());
        Payment newPayment = createPayment(requestDto);
        return new ValidateResponseDto(PAYMENT_URL, newPayment.getId());
    }

    public Payment createPayment(ValidateRequestDto requestDto) {
        Payment payment = Payment.builder().merchantOrderId(requestDto.getMerchantOrderId())
                .merchantTimestamp(requestDto.getMerchantTimestamp())
                .successUrl(requestDto.getSuccessUrl()).failedUrl(requestDto.getFailedUrl()).errorUrl(requestDto.getErrorUrl())
                .transaction(transactionService.createNewTransaction(accountService.getById(UUID.fromString(requestDto.getMerchantId())),
                        requestDto.getAmount(), requestDto.getCurrency()))
                .build();
        return paymentRepository.save(payment);
    }

    public Payment payByCreditCard(CreditCardInfoDto creditCardInfoDto, Long paymentId){
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new NotFoundException(Payment.class.getSimpleName()));
        Optional<CreditCard> creditCard = creditCardService.getCreditCard(creditCardInfoDto.getPan());
        Account account = processPayment(creditCard, payment);
        payment.getTransaction().setIssuer(account);
        return paymentRepository.save(payment);
    }

    private Account processPayment(Optional<CreditCard> creditCard, Payment payment) {
        if(creditCard.isPresent()){
            return accountService.pay(creditCard.get().getAccount(), payment);
        } else {
           return callPcp(payment);
        }
    }

    private Account callPcp(Payment payment) {
        try {
            // TODO: make rest call to PCC to get account from other bank
            return makeRestCall(payment);
        } catch (Exception ex) {
            throw new ErrorInCommunicationException(payment.getErrorUrl());
        }
    }

    private Account makeRestCall(Payment payment) {
        return new Account();
    }


}
