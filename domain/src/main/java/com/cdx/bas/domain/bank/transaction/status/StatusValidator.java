package com.cdx.bas.domain.bank.transaction.status;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusValidator implements ConstraintValidator<ValidStatus, TransactionStatus> {

    private TransactionStatus expectedStatus;
    @Override
    public void initialize(ValidStatus constraintAnnotation) {
        this.expectedStatus = constraintAnnotation.expectedStatus();
    }

    @Override
    public boolean isValid(TransactionStatus value, ConstraintValidatorContext context) {
        if (value != null && value != expectedStatus) {
            String message = "Unexpected transaction status " + value +
                    ", expected status: " + expectedStatus + ".";
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
