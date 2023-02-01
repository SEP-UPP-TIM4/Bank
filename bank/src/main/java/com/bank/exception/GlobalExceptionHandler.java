package com.bank.exception;

import com.bank.dto.RedirectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RedirectDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).toList();
        log.warn(String.valueOf(errors));
        return new ResponseEntity<>(new RedirectDto(errors.get(0)), HttpStatus.BAD_REQUEST);
    }

    private ExceptionResponse getExceptionResponse(String message) {
        ExceptionResponse response = new ExceptionResponse();
        response.setDateTime(LocalDateTime.now());
        response.setMessage(message);
        return response;
    }
}
