package com.cdx.bas.domain.bank.account;

import com.cdx.bas.domain.exception.DomainException;

public class BankAccountException extends DomainException {

    private static final long serialVersionUID = -5539383143022544288L;

    public BankAccountException(String errorMessage) {
        super(errorMessage);
    }

    public BankAccountException(String message, Throwable cause) {
        super(message, cause);
    }

}
