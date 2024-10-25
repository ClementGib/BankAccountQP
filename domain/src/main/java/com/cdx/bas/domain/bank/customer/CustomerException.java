package com.cdx.bas.domain.bank.customer;

import com.cdx.bas.domain.DomainException;

public class CustomerException extends DomainException {
    public CustomerException(String errorMessage) {
        super(errorMessage);
    }

    public CustomerException(String message, Throwable cause) {
        super(message, cause);
    }
}
