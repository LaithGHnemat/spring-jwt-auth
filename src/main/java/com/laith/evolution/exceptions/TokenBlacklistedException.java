package com.laith.evolution.exceptions;

public class TokenBlacklistedException extends RuntimeException {
    public TokenBlacklistedException(String m) {
        super(m);
    }
}
