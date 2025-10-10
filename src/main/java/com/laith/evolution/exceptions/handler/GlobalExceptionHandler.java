package com.laith.evolution.exceptions.handler;

import com.laith.evolution.dto.ErrorResponseDto;
import com.laith.evolution.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidClientCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidClient(InvalidClientCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto("invalid_client", ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedGrantTypeException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedGrant(UnsupportedGrantTypeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("unsupported_grant_type", ex.getMessage()));
    }

    @ExceptionHandler(InvalidScopeException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidScope(InvalidScopeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("invalid_scope", ex.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto("invalid_token", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundClientException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundClient(InvalidTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("server_error", ex.getMessage()));
    }
}
