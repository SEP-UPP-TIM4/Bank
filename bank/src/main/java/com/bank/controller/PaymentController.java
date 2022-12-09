package com.bank.controller;

import com.bank.dto.CreditCardInfoDto;
import com.bank.dto.PaymentResponseDto;
import com.bank.dto.ValidateRequestDto;
import com.bank.dto.ValidateResponseDto;
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
    public PaymentResponseDto payByCreditCard(@RequestBody CreditCardInfoDto requestDto) {
        //TODO: pronaci payment id iz jwt ili putanje
        Long paymentId = 1L;
        // TODO: vratiti PSP-u ove podatke, a kupca redirektovati na successUrl/failedUrl/errorUrl
        return PaymentMapper.PaymentToPaymentResponseDto(paymentService.payByCreditCard(requestDto, paymentId));
    }
}
