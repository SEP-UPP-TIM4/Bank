package com.bank.controller;

import com.bank.dto.*;
import com.bank.model.Payment;
import com.bank.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("validate")
    @ResponseStatus(value = HttpStatus.OK)
    public ValidateResponseDto validate(@RequestBody ValidateRequestDto requestDto) {
        return paymentService.getPaymentUrlAndId(requestDto);
    }

    @CrossOrigin(origins = "http://localhost:4201")
    @PostMapping("credit-card/{paymentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public RedirectDto payByCreditCard(@PathVariable Long paymentId, @RequestBody CreditCardInfoDto requestDto) {
        Payment payment = paymentService.payByCreditCard(requestDto, paymentId);
        // TODO: Convert Payment to PaymentResponseDto and send to PSP
        return new RedirectDto(payment.getSuccessUrl());
    }
}
