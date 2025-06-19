package org.example.exception_handler;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> illegalStateException(IllegalArgumentException e, WebRequest request) {
        log.warn("Global exception handler caught IllegalArgumentException with message: " + e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(
                        ErrorResponse
                                .builder()
                                .message(e.getMessage())
                                .build());
    }
}
