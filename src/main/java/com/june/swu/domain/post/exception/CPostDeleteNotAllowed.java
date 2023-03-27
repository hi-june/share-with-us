package com.june.swu.domain.post.exception;

public class CPostDeleteNotAllowed extends RuntimeException {

    public CPostDeleteNotAllowed(String message, Throwable cause) {
        super(message, cause);
    }

    public CPostDeleteNotAllowed(String message) {
        super(message);
    }

    public CPostDeleteNotAllowed() {
        super();
    }
}
