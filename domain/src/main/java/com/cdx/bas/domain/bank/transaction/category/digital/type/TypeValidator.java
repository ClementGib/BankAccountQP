package com.cdx.bas.domain.bank.transaction.category.digital.type;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TypeValidator implements ConstraintValidator<ValidType, TransactionType> {

    TransactionType[] expectedTypes;

    @Override
    public void initialize(ValidType constraintAnnotation) {
        expectedTypes = constraintAnnotation.expectedTypes();
    }

    @Override
    public boolean isValid(TransactionType value, ConstraintValidatorContext context) {
            if (value != null && !Arrays.asList(expectedTypes).contains(value)) {
            String message = "Unexpected transaction types " + value +
                    ", expected type: " + Arrays.stream(expectedTypes)
                    .map(TransactionType::name)
                    .collect(Collectors.joining(", "))+ ".";

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
