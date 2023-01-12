package com.bank.service;

import com.bank.dto.*;
import com.bank.exception.ErrorInCommunicationException;
import com.bank.exception.NotFoundException;
import com.bank.model.*;
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
    private final PccResponseService pccResponseService;

    private static final String PAYMENT_URL = "http://localhost:4201/payment";
    private static final String QR_PAYMENT_URL = "http://localhost:4201/qr-payment";
    public static final String FINISH_URL = "http://localhost:8081/CREDIT-CARD-SERVICE/api/v1/payment/finish";


    public PaymentService(TransactionService transactionService, PaymentRepository paymentRepository,
                          AccountService accountService, CreditCardService creditCardService, RestTemplate restTemplate, PccResponseService pccResponseService) {
        this.transactionService = transactionService;
        this.paymentRepository = paymentRepository;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.restTemplate = restTemplate;
        this.pccResponseService = pccResponseService;
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
        Transaction acquirerTransaction = transactionService.createAcquirerTransaction(accountService.getById(UUID.fromString(requestDto.getMerchantId())),
                requestDto.getAmount(), requestDto.getCurrency());
        Payment payment = Payment.builder().merchantOrderId(requestDto.getMerchantOrderId())
                .merchantTimestamp(requestDto.getMerchantTimestamp())
                .successUrl(requestDto.getSuccessUrl()).failedUrl(requestDto.getFailedUrl()).errorUrl(requestDto.getErrorUrl())
                .acquirerTransaction(acquirerTransaction)
                .build();
        log.info("Acquirer transaction with id {} successfully created", acquirerTransaction.getId());
        return paymentRepository.save(payment);
    }

    public Payment payByCreditCard(CreditCardInfoDto creditCardInfoDto, Long paymentId){
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new NotFoundException(Payment.class.getSimpleName()));
        processPayment(creditCardInfoDto, payment);
        return paymentRepository.save(payment);
    }

    private void processPayment(CreditCardInfoDto creditCardInfoDto, Payment payment) {
        Optional<CreditCard> creditCard = creditCardService.getCreditCard(creditCardInfoDto);
        if(creditCard.isEmpty())
            callPcc(creditCardInfoDto, payment);
        else {
            Account issuer = accountService.pay(creditCard.get().getAccount(), payment);
            Transaction issuerTransaction = createIssuerTransaction(payment, issuer);
            payment.setIssuerTransaction(issuerTransaction);
        }
    }

    private void callPcc(CreditCardInfoDto creditCardInfo, Payment payment) {
        try {
            PccResponseDto response = pccResponseService.makeRestCallToPcc(creditCardInfo, payment);
            payment.getAcquirerTransaction().setStatus(response.isSuccess() ? TransactionStatus.PROCESSED : TransactionStatus.FAILED);
            if(response.isSuccess()) {
                Transaction issuerTransaction = createIssuerTransaction(payment, null);
                payment.setIssuerTransaction(issuerTransaction);
            }
        } catch (Exception ex) {
            payment.getAcquirerTransaction().setStatus(TransactionStatus.FAILED);
            paymentRepository.save(payment);
            throw new ErrorInCommunicationException(payment.getErrorUrl());
        }
    }

    private Transaction createIssuerTransaction(Payment payment, Account issuer) {
        Transaction transaction = transactionService.createIssuerTransaction(issuer,
                payment.getAcquirerTransaction().getAmount(), payment.getAcquirerTransaction().getCurrency());
        transaction.setStatus(TransactionStatus.PROCESSED);
        return transaction;
    }

    public void finishPayment(Payment payment) {
        PaymentResponseDto paymentDto = PaymentResponseDto.builder()
                .merchantOrderId(payment.getMerchantOrderId())
                .acquirerOrderId(payment.getAcquirerTransaction().getId())
                .acquirerTimestamp(payment.getAcquirerTransaction().getTimestamp())
                .paymentId(payment.getId())
                .transactionStatus(payment.getAcquirerTransaction().getStatus().toString())
                .transactionAmount(payment.getAcquirerTransaction().getAmount()).build();
        try {
            // TODO: try to get this url from database or somewhere else
            restTemplate.postForObject(FINISH_URL, paymentDto, PaymentResponseDto.class);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    public Payment findById(Long id){
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Payment.class.getSimpleName()));
    }

    public Payment save(Payment payment){
        return paymentRepository.save(payment);
    }
}
