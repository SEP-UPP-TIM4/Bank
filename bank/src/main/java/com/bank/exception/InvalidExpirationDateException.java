package com.bank.exception;

public class InvalidExpirationDateException extends RuntimeException{

    public InvalidExpirationDateException() {
        super("Invalid expiration date!");
    }
}
