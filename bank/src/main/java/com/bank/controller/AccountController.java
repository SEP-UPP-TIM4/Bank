package com.bank.controller;

import com.bank.dto.PaymentRequestDto;
import com.bank.dto.PaymentResponseDto;
import com.bank.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping(value = "api/v1/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("validate")
    @ResponseStatus(value = HttpStatus.OK)
    public PaymentResponseDto validate(@RequestBody PaymentRequestDto requestDto) throws AccountNotFoundException {
        return accountService.getPaymentUrlAndId(requestDto);
    }
}
