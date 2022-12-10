package com.bank.exception;

public class ErrorInCommunicationException extends RuntimeException{

    public ErrorInCommunicationException(String errorUrl) {
        super(errorUrl);
    }
}
