package com.cdx.bas.domain.money;

import java.math.BigDecimal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Saving amount validator
 */
public class AmountValidator implements ConstraintValidator<Amount, Money> {
    private BigDecimal max;
    private BigDecimal min;

    @Override
    public void initialize(Amount constraintAnnotation) {
        this.max = new BigDecimal(constraintAnnotation.max());
        this.min = new BigDecimal(constraintAnnotation.min());
    }

    @Override
    public boolean isValid(Money money, ConstraintValidatorContext context) {
        return money != null
                && (money.getAmount().compareTo(min) >= 0 && money.getAmount().compareTo(max) <= 0);
    }

}
