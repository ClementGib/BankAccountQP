package com.cdx.bas.application.bank.customer.maritalstatus;

import org.junit.jupiter.api.Test;

import static com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class MaritalStatusConverterTest {

    private final MaritalStatusConverter maritalStatusConverter = new MaritalStatusConverter();

    @Test
    void convertToDatabaseColumn_shouldConvertToMaritalCode() {
        assertEquals(STR_SINGLE, maritalStatusConverter.convertToDatabaseColumn(SINGLE));
        assertEquals(STR_MARRIED, maritalStatusConverter.convertToDatabaseColumn(MARRIED));
        assertEquals(STR_WIDOWED, maritalStatusConverter.convertToDatabaseColumn(WIDOWED));
        assertEquals(STR_DIVORCED, maritalStatusConverter.convertToDatabaseColumn(DIVORCED));
        assertEquals(STR_PACS, maritalStatusConverter.convertToDatabaseColumn(PACS));
    }

    @Test
    void convertToDatabaseColumn_shouldReturnNull_whenMaritalStatusIsNull() {
        assertNull(maritalStatusConverter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttribute_shouldConvertToMaritalStatus() {
        assertEquals(SINGLE, maritalStatusConverter.convertToEntityAttribute(STR_SINGLE));
        assertEquals(MARRIED, maritalStatusConverter.convertToEntityAttribute(STR_MARRIED));
        assertEquals(WIDOWED, maritalStatusConverter.convertToEntityAttribute(STR_WIDOWED));
        assertEquals(DIVORCED, maritalStatusConverter.convertToEntityAttribute(STR_DIVORCED));
        assertEquals(PACS, maritalStatusConverter.convertToEntityAttribute(STR_PACS));
    }

    @Test
    void convertToEntityAttribute_shouldReturnNull_whenMaritalCodeIsNull() {
        assertNull(maritalStatusConverter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityAttribute_shouldThrowExceptionForInvalidDatabaseColumn() {
        assertThrows(IllegalStateException.class, () -> maritalStatusConverter.convertToEntityAttribute('X'));
    }
}
