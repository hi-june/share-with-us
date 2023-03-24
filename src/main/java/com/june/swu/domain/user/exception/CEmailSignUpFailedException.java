package com.june.swu.domain.user.exception;

public class CEmailSignUpFailedException extends RuntimeException{
    public CEmailSignUpFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CEmailSignUpFailedException(String message) {
        super(message);
    }

    public CEmailSignUpFailedException() {
        super();
    }
}
