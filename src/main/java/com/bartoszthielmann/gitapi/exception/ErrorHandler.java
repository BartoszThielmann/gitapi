package com.bartoszthielmann.gitapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<NotFoundExceptionResponse> handleUserNotFoundException(UserNotFoundException e) {
        NotFoundExceptionResponse res =
                new NotFoundExceptionResponse(HttpStatus.NOT_FOUND.value(), "User does not exist");
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }
}
