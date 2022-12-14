package com.bank.exception;

public class WrongCardholderNameException extends RuntimeException{

    public WrongCardholderNameException() {
        super("Wrong cardholder name!");
    }
}
