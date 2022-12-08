package com.bank.exception;

public class NotEnoughFundsException extends RuntimeException{
    public NotEnoughFundsException() {
        super("Insufficient funds on the account!");
    }
}
