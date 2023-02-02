package com.bank.controller;

import com.bank.dto.*;
import com.bank.model.Payment;
import com.bank.model.Status;
import com.bank.service.PaymentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public static final String FINISH_URL = "http://" + System.getenv("ip_address2") + ":8081/CREDIT-CARD-SERVICE/api/v1/payment/finish";

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("validate")
    @ResponseStatus(value = HttpStatus.OK)
    public ValidateResponseDto validate(@RequestBody ValidationRequestDto requestDto) {
        return paymentService.getPaymentUrlAndId(requestDto);
    }

    @PostMapping("validate-qr")
    @ResponseStatus(value = HttpStatus.OK)
    public ValidateResponseDto validateQr(@RequestBody ValidationRequestDto requestDto) {
        return paymentService.getPaymentUrlAndIdQr(requestDto);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("credit-card/{paymentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public RedirectDto payByCreditCard(@PathVariable UUID paymentId, @RequestBody @Valid CreditCardInfoDto requestDto) {
        Payment payment = paymentService.payByCreditCard(requestDto, paymentId);
        paymentService.finishPayment(payment, FINISH_URL);
        log.info("Payment with id {} successfully finished!", paymentId);
        return new RedirectDto(payment.getStatus() == Status.PROCESSED ? payment.getSuccessUrl() : payment.getFailedUrl());
    }

    @PostMapping("pcc")
    @ResponseStatus(HttpStatus.OK)
    public PccResponseDto processPccRequest(@RequestBody PccRequestDto requestDto) {
       return paymentService.processExternalPayment(requestDto);
    }
}
