package com.bank.service;

import com.bank.dto.*;
import com.bank.exception.ErrorInCommunicationException;
import com.bank.exception.NotEnoughFundsException;
import com.bank.exception.NotFoundException;
import com.bank.model.*;
import com.bank.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.http.HttpResponse;
import java.util.Arrays;
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

    public PaymentService(TransactionService transactionService, PaymentRepository paymentRepository,
                          AccountService accountService, CreditCardService creditCardService,
                          RestTemplate restTemplate, PccResponseService pccResponseService) {
        this.transactionService = transactionService;
        this.paymentRepository = paymentRepository;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.restTemplate = restTemplate;
        this.pccResponseService = pccResponseService;
    }

    public ValidateResponseDto getPaymentUrlAndId(ValidationRequestDto requestDto) {
        accountService.validateAccount(UUID.fromString(requestDto.getMerchantId()), requestDto.getMerchantPassword());
        Payment newPayment = createPayment(requestDto);
        log.info("Payment with id {} successfully created", newPayment.getId());
        return new ValidateResponseDto(PAYMENT_URL, newPayment.getId());
    }

    public ValidateResponseDto getPaymentUrlAndIdQr(ValidationRequestDto requestDto) {
        accountService.validateAccount(UUID.fromString(requestDto.getMerchantId()), requestDto.getMerchantPassword());
        Payment newPayment = createPayment(requestDto);
        log.info("Payment with id {} successfully created", newPayment.getId());
        return new ValidateResponseDto(QR_PAYMENT_URL, newPayment.getId());
    }

    public Payment createPayment(ValidationRequestDto requestDto) {
        Transaction acquirerTransaction = transactionService.createAcquirerTransaction(accountService.getById(UUID.fromString(requestDto.getMerchantId())),
                requestDto.getAmount(), requestDto.getCurrency());
        Payment payment = Payment.builder()
                .merchantOrderId(requestDto.getMerchantOrderId())
                .merchantTimestamp(requestDto.getMerchantTimestamp())
                .successUrl(requestDto.getSuccessUrl()).failedUrl(requestDto.getFailedUrl())
                .errorUrl(requestDto.getErrorUrl())
                .status(Status.PENDING)
                .acquirerTransaction(acquirerTransaction)
                .build();
        log.info("Acquirer transaction with id {} successfully created", acquirerTransaction.getId());
        return paymentRepository.save(payment);
    }

    public Payment payByCreditCard(CreditCardInfoDto creditCardInfoDto, UUID paymentId){
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new NotFoundException(Payment.class.getSimpleName()));
        return processPayment(creditCardInfoDto, payment);
    }

    public Payment processPayment(CreditCardInfoDto creditCardInfoDto, Payment payment) {
        Optional<CreditCard> creditCard = creditCardService.getCreditCard(creditCardInfoDto);
        if(creditCard.isPresent()){
            return processInternalPayment(creditCard.get().getAccount(), payment);
        } else {
            return callPcc(creditCardInfoDto, payment);
        }
    }

    public Payment processInternalPayment(Account issuer, Payment payment) throws NotEnoughFundsException {
        int compareResult = issuer.getAmount().compareTo(payment.getAcquirerTransaction().getAmount());
        if(compareResult < 0){
            payment.getAcquirerTransaction().setStatus(Status.FAILED);
            payment.setStatus(Status.FAILED);
            paymentRepository.save(payment);
            throw new NotEnoughFundsException(payment.getFailedUrl());
        }
        Transaction issuerTransaction = transactionService.createIssuerTransaction(issuer,
                payment.getAcquirerTransaction().getAmount(),
                payment.getAcquirerTransaction().getCurrency());
        payment.setIssuerTransaction(issuerTransaction);
        accountService.increaseAmount(payment.getAcquirerTransaction().getAccount(), payment.getAcquirerTransaction().getAmount());
        payment.getAcquirerTransaction().setStatus(Status.PROCESSED);
        payment.setStatus(Status.PROCESSED);
        return paymentRepository.save(payment);
    }

    public Payment callPcc(CreditCardInfoDto creditCardInfo, Payment payment) {
        try {
            PccResponse response = pccResponseService.makeRestCallToPcc(creditCardInfo, payment);
            payment.getAcquirerTransaction().setStatus(response.isSuccess() ? Status.PROCESSED : Status.FAILED);
            payment.setStatus(response.isSuccess() ? Status.PROCESSED : Status.FAILED);
            if(response.isSuccess())
                accountService.increaseAmount(payment.getAcquirerTransaction().getAccount(), payment.getAcquirerTransaction().getAmount());
            return paymentRepository.save(payment);
        } catch (Exception ex) {
            payment.getAcquirerTransaction().setStatus(Status.FAILED);
            payment.setStatus(Status.FAILED);
            paymentRepository.save(payment);
            throw new ErrorInCommunicationException(payment.getErrorUrl());
        }
    }

    public void finishPayment(Payment payment, String finishUrl) {
        PaymentResponseDto paymentDto = PaymentResponseDto.builder()
                .merchantOrderId(payment.getMerchantOrderId())
                .acquirerOrderId(payment.getAcquirerTransaction().getId())
                .acquirerTimestamp(payment.getAcquirerTransaction().getTimestamp())
                .paymentId(payment.getId())
                .transactionStatus(payment.getAcquirerTransaction().getStatus().toString())
                .transactionAmount(payment.getAcquirerTransaction().getAmount()).build();
        try {
            HttpResponse response = restTemplate.postForObject(finishUrl, paymentDto, HttpResponse.class);
            if(response!= null && response.statusCode() != 200)
                finishPayment(payment, finishUrl);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    public Payment findById(UUID id){
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Payment.class.getSimpleName()));
    }

    public Payment save(Payment payment){
        return paymentRepository.save(payment);
    }

    public PccResponseDto processExternalPayment(PccRequestDto requestDto) {
        Transaction issuerTransaction = makeIssuerTransaction(requestDto);
        return PccResponseDto.builder()
                .acquirerOrderId(requestDto.getAcquirerOrderId())
                .acquirerTimestamp(requestDto.getAcquirerTimestamp())
                .issuerOrderId(issuerTransaction != null ? issuerTransaction.getId() : null)
                .issuerTimestamp(issuerTransaction != null ? issuerTransaction.getTimestamp() : null)
                .success(issuerTransaction != null).build();
    }

    private Transaction makeIssuerTransaction(PccRequestDto requestDto) {
        Optional<CreditCard> creditCard = findCreditCard(requestDto);
        Transaction issuerTransaction = null;
        if(creditCard.isPresent()) {
            Account issuer = creditCard.get().getAccount();
            if(haveEnoughFunds(issuer, requestDto.getAmount()))
                issuerTransaction = transactionService.createIssuerTransaction(creditCard.get().getAccount(),
                    requestDto.getAmount(), requestDto.getCurrency());
        }
        return issuerTransaction;
    }

    private boolean haveEnoughFunds(Account issuer, BigDecimal amount) {
        return issuer.getAmount().compareTo(amount) > 0;
    }

    private Optional<CreditCard> findCreditCard(PccRequestDto requestDto) {
        CreditCardInfoDto creditCardInfoDto = CreditCardInfoDto.builder()
                .cardholderName(requestDto.getCardholderName())
                .pan(requestDto.getPan())
                .cvv(requestDto.getCvv())
                .expirationDate(requestDto.getExpirationDate())
                .build();
        return creditCardService.getCreditCard(creditCardInfoDto);
    }

}
