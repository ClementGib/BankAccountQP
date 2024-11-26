package com.cdx.bas.domain.currency.validation;

import com.cdx.bas.domain.currency.rate.ExchangeRateUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CurrencyValidator implements ConstraintValidator<ValidCurrency, String> {

    @Override
    public boolean isValid(String currency, ConstraintValidatorContext context) {
        return ExchangeRateUtils.hasCurrency(currency);
    }
}
