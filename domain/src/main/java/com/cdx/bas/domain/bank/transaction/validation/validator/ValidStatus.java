package com.cdx.bas.domain.bank.transaction.validation.validator;

import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.validation.validator.StatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StatusValidator.class)
@Documented
public @interface ValidStatus {

    TransactionStatus expectedStatus() default TransactionStatus.UNPROCESSED;
    String message() default "Transaction status is invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
