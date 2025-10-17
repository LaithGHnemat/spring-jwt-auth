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
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto("invalid_client", ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedGrantTypeException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedGrant(UnsupportedGrantTypeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("unsupported_grant_type", ex.getMessage()));
    }

    @ExceptionHandler(InvalidScopeException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidScope(InvalidScopeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("invalid_scope", ex.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto("invalid_token", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundClientException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundClient(NotFoundClientException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto("invalid_refresh_token", ex.getMessage()));
    }
    @ExceptionHandler(TokenBlacklistedException.class)
    public ResponseEntity<ErrorResponseDto> handleTokenBlacklisted(TokenBlacklistedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto("token_blacklisted", ex.getMessage()));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleRateLimitExceeded(RateLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS) // 429
                .body(new ErrorResponseDto("rate_limit_exceeded", ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("server_error", ex.getMessage()));
    }
}
