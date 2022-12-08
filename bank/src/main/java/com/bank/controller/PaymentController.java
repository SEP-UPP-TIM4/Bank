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
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
        this.paymentMapper = new PaymentMapper();
    }

    @PostMapping("validate")
    @ResponseStatus(value = HttpStatus.OK)
    public ValidateResponseDto validate(@RequestBody ValidateRequestDto requestDto) {
        return paymentService.getPaymentUrlAndId(requestDto);
    }

    @PostMapping("/credit-card")
    @ResponseStatus(value = HttpStatus.OK)
    public PaymentResponseDto payByCreditCard(@RequestBody CreditCardInfoDto requestDto) {
        //pronaci payment id iz jwt
        Long paymentId = Long.valueOf(1);
        return PaymentMapper.PaymentToPaymentResponseDto(paymentService.payByCreditCard(requestDto, paymentId));
    }
}
