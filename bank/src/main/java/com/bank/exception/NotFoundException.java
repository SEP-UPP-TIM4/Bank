package com.bank.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String resource) {
        super(resource + " not found!");
    }
}
