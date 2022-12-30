package com.bank.exception;

public class QrCodeNotValidException extends RuntimeException{
    public QrCodeNotValidException(String invalid) {
        super("Qr code not valid. "+ invalid);
    }
}
