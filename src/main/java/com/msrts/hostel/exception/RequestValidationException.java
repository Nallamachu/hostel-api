package com.msrts.hostel.exception;

import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RequestValidationException extends Throwable {

    @ExceptionHandler(value = {ConfigDataResourceNotFoundException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errorMessages = ((MethodArgumentNotValidException) ex)
                .getBindingResult()
                .getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity(errorMessages.toString(), HttpStatus.BAD_REQUEST);
    }

}