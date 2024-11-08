package com.cdx.bas.domain.bank.account.validation;

import java.util.Set;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@RequestScoped
public class BankAccountValidator {

    Validator validator;

    @Inject
    public BankAccountValidator(Validator validator) {
        this.validator = validator;
    }

    public void validateBankAccount(BankAccount bankAccount) throws BankAccountException {
        Set<ConstraintViolation<BankAccount>> violations = validator.validate(bankAccount);
        if (!violations.isEmpty()) {
            throw new BankAccountException(concatViolations(violations));
        }
    }
    
    private static String concatViolations(Set<ConstraintViolation<BankAccount>> violations) {
        StringBuilder violationBuilder = new StringBuilder();
        for (ConstraintViolation<BankAccount> violation : violations) {
            violationBuilder.append(violation.getMessage());
            violationBuilder.append("\n");
        }
        return violationBuilder.toString();
    }
}
