package com.iprody.leonidm.paymentserviceapp.controller;

import com.iprody.leonidm.paymentserviceapp.dto.ErrorDto;
import com.iprody.leonidm.paymentserviceapp.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleCommonException(Exception e) {
        return new ErrorDto(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFound(EntityNotFoundException ex) {
        return new ErrorDto(
            ex.getMessage(),
            Instant.now(),
            ex.getEntityId(),
            ex.getOperation()
        );
    }
}
