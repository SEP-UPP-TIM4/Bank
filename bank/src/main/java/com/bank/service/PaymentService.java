package com.bank.service;

import com.bank.dto.CreditCardInfoDto;
import com.bank.dto.PaymentResponseDto;
import com.bank.dto.ValidateRequestDto;
import com.bank.dto.ValidateResponseDto;
import com.bank.exception.ErrorInCommunicationException;
import com.bank.exception.NotFoundException;
import com.bank.model.Account;
import com.bank.model.CreditCard;
import com.bank.model.Payment;
import com.bank.model.Transaction;
import com.bank.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PaymentService {

    private final TransactionService transactionService;
    private final PaymentRepository paymentRepository;
    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final RestTemplate restTemplate;

    private final String PAYMENT_URL = "http://localhost:4201/payment";
    private final String QR_PAYMENT_URL = "http://localhost:4201/qr-payment";

    public PaymentService(TransactionService transactionService, PaymentRepository paymentRepository,
                          AccountService accountService, CreditCardService creditCardService, RestTemplate restTemplate) {
        this.transactionService = transactionService;
        this.paymentRepository = paymentRepository;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.restTemplate = restTemplate;
    }

    public ValidateResponseDto getPaymentUrlAndId(ValidateRequestDto requestDto) {
        accountService.validateAccount(UUID.fromString(requestDto.getMerchantId()), requestDto.getMerchantPassword());
        Payment newPayment = createPayment(requestDto);
        log.info("Payment with id {} successfully created", newPayment.getId());
        return new ValidateResponseDto(PAYMENT_URL, newPayment.getId());
    }

    public ValidateResponseDto getPaymentUrlAndIdQr(ValidateRequestDto requestDto) {
        accountService.validateAccount(UUID.fromString(requestDto.getMerchantId()), requestDto.getMerchantPassword());
        Payment newPayment = createPayment(requestDto);
        log.info("Payment with id {} successfully created", newPayment.getId());
        return new ValidateResponseDto(QR_PAYMENT_URL, newPayment.getId());
    }

    public Payment createPayment(ValidateRequestDto requestDto) {
        Transaction newTransaction = transactionService.createNewTransaction(accountService.getById(UUID.fromString(requestDto.getMerchantId())),
                requestDto.getAmount(), requestDto.getCurrency());
        Payment payment = Payment.builder().merchantOrderId(requestDto.getMerchantOrderId())
                .merchantTimestamp(requestDto.getMerchantTimestamp())
                .successUrl(requestDto.getSuccessUrl()).failedUrl(requestDto.getFailedUrl()).errorUrl(requestDto.getErrorUrl())
                .transaction(newTransaction)
                .build();
        log.info("Transaction with id {} successfully created", newTransaction.getId());
        return paymentRepository.save(payment);
    }

    public Payment payByCreditCard(CreditCardInfoDto creditCardInfoDto, Long paymentId){
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new NotFoundException(Payment.class.getSimpleName()));
        Optional<CreditCard> creditCard = creditCardService.getCreditCard(creditCardInfoDto);
        Account account = processPayment(creditCard, payment);
        payment.getTransaction().setIssuer(account);
        return paymentRepository.save(payment);
    }

    public void finishPayment(Payment payment) {
        PaymentResponseDto paymentDto = new PaymentResponseDto();
        paymentDto.setMerchantOrderId(payment.getMerchantOrderId());
        paymentDto.setAcquirerOrderId(payment.getTransaction().getId());
        paymentDto.setAcquirerTimestamp(payment.getTransaction().getTimeStamp());
        paymentDto.setPaymentId(payment.getId());
        paymentDto.setTransactionStatus(payment.getTransaction().getStatus().toString());
        paymentDto.setTransactionAmount(payment.getTransaction().getAmount());
        try {
            // TODO: try to get this url from database
            restTemplate.postForObject("http://localhost:8081/CREDIT-CARD-SERVICE/api/v1/payment/finish", paymentDto, PaymentResponseDto.class);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

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

    public Payment findById(Long id){
        Optional<Payment> payment = paymentRepository.findById(id);
        if(!payment.isPresent()) throw new NotFoundException(Payment.class.getSimpleName());
        return payment.get();
    }

    public Payment save(Payment payment){
        return paymentRepository.save(payment);
    }

    private Account makeRestCall(Payment payment) {
        return new Account();
    }
}
