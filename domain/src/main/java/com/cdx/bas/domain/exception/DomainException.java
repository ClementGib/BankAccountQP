package com.cdx.bas.domain.exception;


import com.cdx.bas.domain.testing.Generated;

public class DomainException extends RuntimeException {

    @Generated
    public DomainException(String errorMessage) {
        super(errorMessage);
    }

    @Generated
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

}
