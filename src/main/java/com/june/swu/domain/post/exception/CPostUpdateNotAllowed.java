package com.june.swu.domain.post.exception;

public class CPostUpdateNotAllowed extends RuntimeException {

    public CPostUpdateNotAllowed(String message, Throwable cause) {
        super(message, cause);
    }

    public CPostUpdateNotAllowed(String message) {
        super(message);
    }

    public CPostUpdateNotAllowed() {
        super();
    }
}
