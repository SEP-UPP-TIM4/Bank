package com.bank.exception;

public class BadCvvException extends RuntimeException{

    public BadCvvException() {
        super("Bad security code!");
    }
}
