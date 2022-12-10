package com.bank.exception;

import com.bank.dto.RedirectDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {
        ExceptionResponse response = getExceptionResponse(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotEnoughFundsException.class)
    public ResponseEntity<RedirectDto> handleNotEnoughFundsException(NotEnoughFundsException exception) {
        return new ResponseEntity<>(new RedirectDto(exception.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(ErrorInCommunicationException.class)
    public ResponseEntity<RedirectDto> handleNotEnoughFundsException(ErrorInCommunicationException exception) {
        return new ResponseEntity<>(new RedirectDto(exception.getMessage()), HttpStatus.OK);
    }

    private ExceptionResponse getExceptionResponse(String message) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage(message);
        return response;
    }
}
