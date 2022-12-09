package com.bank.controller;

import com.bank.dto.*;
import com.bank.mapper.PaymentMapper;
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

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/credit-card")
    @ResponseStatus(value = HttpStatus.OK)
    public RedirectDto payByCreditCard(@RequestBody CreditCardInfoDto requestDto) {
        //TODO: pronaci payment id iz jwt ili putanje
        Long paymentId = 1L;
        // TODO: vratiti PSP-u ove podatke, a kupca redirektovati na successUrl/failedUrl/errorUrl
        //return PaymentMapper.PaymentToPaymentResponseDto(paymentService.payByCreditCard(requestDto, paymentId));
        paymentService.payByCreditCard(requestDto, paymentId);
        return new RedirectDto("http://localhost:4200/redirect/1");
    }
}
