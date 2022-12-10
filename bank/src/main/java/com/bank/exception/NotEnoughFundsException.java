package com.bank.exception;

public class NotEnoughFundsException extends RuntimeException {
    public NotEnoughFundsException(String redirectUrl) {
        super(redirectUrl);
    }
}
