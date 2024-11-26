package com.cdx.bas.domain.currency.error;


import com.cdx.bas.domain.testing.Generated;

import java.io.Serial;

public class CurrencyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4661867226831802143L;

    @Generated
    public CurrencyException(String errorMessage) {
        super(errorMessage);
    }
}

