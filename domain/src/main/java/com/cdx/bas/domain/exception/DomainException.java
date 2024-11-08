package com.cdx.bas.domain.exception;

public class DomainException extends RuntimeException {

    public DomainException(String errorMessage) {
        super(errorMessage);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

}
