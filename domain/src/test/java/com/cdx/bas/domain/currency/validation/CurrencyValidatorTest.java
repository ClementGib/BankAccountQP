package com.cdx.bas.domain.currency.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyValidatorTest {

    private CurrencyValidator currencyValidator;

    @BeforeEach
    void setUp() {
        currencyValidator = new CurrencyValidator();
    }

    @Test
    void isValid_shouldReturnTrueForValidCurrency() {
        // Test valid currencies
        assertTrue(currencyValidator.isValid("USD", null), "USD should be valid");
        assertTrue(currencyValidator.isValid("EUR", null), "EUR should be valid (pivot currency)");
        assertTrue(currencyValidator.isValid("JPY", null), "JPY should be valid");
    }

    @Test
    void isValid_shouldReturnFalseForInvalidCurrency() {
        // Test invalid currencies
        assertFalse(currencyValidator.isValid("ABC", null), "ABC should be invalid");
        assertFalse(currencyValidator.isValid("XYZ", null), "XYZ should be invalid");
        assertFalse(currencyValidator.isValid(null, null), "null should be invalid");
    }
}