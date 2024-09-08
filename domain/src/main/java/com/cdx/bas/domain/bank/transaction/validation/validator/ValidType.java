package com.cdx.bas.domain.bank.transaction.validation.validator;

import com.cdx.bas.domain.bank.transaction.type.TransactionType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Repeatable(ValidType.TypeContainer.class)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TypeValidator.class)
public @interface ValidType {

    TransactionType[] expectedTypes();
    String message() default "Transaction type is invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface TypeContainer {
        ValidType[] value();
    }
}
