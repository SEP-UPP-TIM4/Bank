package com.bank.controller;

import com.bank.dto.*;
import com.bank.model.Payment;
import com.bank.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

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

    @CrossOrigin(origins = "http://localhost:4201")
    @PostMapping("credit-card/{paymentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public RedirectDto payByCreditCard(@PathVariable Long paymentId, @RequestBody CreditCardInfoDto requestDto) {
        Payment payment = paymentService.payByCreditCard(requestDto, paymentId);
        paymentService.finishPayment(payment);
        log.info("Payment with id {} successfully finished!", paymentId);
        return new RedirectDto(payment.getSuccessUrl());
    }

    @PostMapping("pcc")
    @ResponseStatus(HttpStatus.OK)
    public PccResponseDto receive(@RequestBody PccRequestDto requestDto) {
       return paymentService.processExternalPayment(requestDto);
    }
}
