package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.exception.DomainException;

import java.io.Serial;

public class TransactionException extends DomainException {

    @Serial
    private static final long serialVersionUID = 9064189216525457809L;

	public TransactionException(String errorMessage) {
        super(errorMessage);
    }


    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

}
