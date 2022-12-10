package com.bank.exception;

public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException(String merchantId) {
        super("Bad credentials for merchant id: "+ merchantId);
    }
}
