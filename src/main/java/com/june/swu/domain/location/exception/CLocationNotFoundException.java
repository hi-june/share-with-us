package com.june.swu.domain.location.exception;

public class CLocationNotFoundException extends RuntimeException {

    public CLocationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CLocationNotFoundException(String message) {
        super(message);
    }

    public CLocationNotFoundException() {
        super();
    }
}
