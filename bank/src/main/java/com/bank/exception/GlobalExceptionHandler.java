package com.bank.exception;

import com.bank.dto.RedirectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {
        ExceptionResponse response = getExceptionResponse(exception.getMessage());
        log.warn(exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotEnoughFundsException.class)
    public ResponseEntity<RedirectDto> handleNotEnoughFundsException(NotEnoughFundsException exception) {
        log.warn(exception.getMessage());
        return new ResponseEntity<>(new RedirectDto(exception.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(ErrorInCommunicationException.class)
    public ResponseEntity<RedirectDto> handleErrorInCommunicationException(ErrorInCommunicationException exception) {
        log.warn("Error in communication.");
        return new ResponseEntity<>(new RedirectDto(exception.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(WrongCardholderNameException.class)
    public ResponseEntity<RedirectDto> handleInvalidWrongCardholderNameException(WrongCardholderNameException exception) {
        log.warn(exception.getMessage());
        return new ResponseEntity<>(new RedirectDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidExpirationDateException.class)
    public ResponseEntity<RedirectDto> handleInvalidExpirationDateException(InvalidExpirationDateException exception) {
        log.warn(exception.getMessage());
        return new ResponseEntity<>(new RedirectDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCvvException.class)
    public ResponseEntity<RedirectDto> handleBadCvvException(BadCvvException exception) {
        log.warn(exception.getMessage());
        return new ResponseEntity<>(new RedirectDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    private ExceptionResponse getExceptionResponse(String message) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage(message);
        return response;
    }
}
