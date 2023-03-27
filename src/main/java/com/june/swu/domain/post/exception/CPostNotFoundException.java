package com.june.swu.domain.post.exception;

public class CPostNotFoundException extends RuntimeException {

    public CPostNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CPostNotFoundException(String message) {
        super(message);
    }

    public CPostNotFoundException() {
        super();
    }
}
