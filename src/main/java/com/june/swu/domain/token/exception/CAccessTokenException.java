package com.june.swu.domain.token.exception;

public class CAccessTokenException extends RuntimeException {
    public CAccessTokenException() {
        super();
    }

    public CAccessTokenException(String message) {
        super(message);
    }

    public CAccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
