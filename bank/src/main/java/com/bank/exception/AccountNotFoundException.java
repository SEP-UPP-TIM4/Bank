package com.bank.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountId) {
        super(String.format("Account with id: %s not found!", accountId));
    }

}
