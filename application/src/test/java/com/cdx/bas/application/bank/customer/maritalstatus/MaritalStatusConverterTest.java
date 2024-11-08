package com.cdx.bas.application.bank.customer.maritalstatus;

import org.junit.jupiter.api.Test;

import static com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class MaritalStatusConverterTest {

    private final MaritalStatusConverter maritalStatusConverter = new MaritalStatusConverter();

    @Test
    public void convertToDatabaseColumn_shouldConvertToMaritalCode() {
        assertEquals(strSingle, maritalStatusConverter.convertToDatabaseColumn(SINGLE));
        assertEquals(strMarried, maritalStatusConverter.convertToDatabaseColumn(MARRIED));
        assertEquals(strWidowed, maritalStatusConverter.convertToDatabaseColumn(WIDOWED));
        assertEquals(strDivorced, maritalStatusConverter.convertToDatabaseColumn(DIVORCED));
        assertEquals(strPacs, maritalStatusConverter.convertToDatabaseColumn(PACS));
    }

    @Test
    public void convertToDatabaseColumn_shouldReturnNull_whenMaritalStatusIsNull() {
        assertNull(maritalStatusConverter.convertToDatabaseColumn(null));
    }

    @Test
    public void convertToEntityAttribute_shouldConvertToMaritalStatus() {
        assertEquals(SINGLE, maritalStatusConverter.convertToEntityAttribute(strSingle));
        assertEquals(MARRIED, maritalStatusConverter.convertToEntityAttribute(strMarried));
        assertEquals(WIDOWED, maritalStatusConverter.convertToEntityAttribute(strWidowed));
        assertEquals(DIVORCED, maritalStatusConverter.convertToEntityAttribute(strDivorced));
        assertEquals(PACS, maritalStatusConverter.convertToEntityAttribute(strPacs));
    }

    @Test
    public void convertToEntityAttribute_shouldReturnNull_whenMaritalCodeIsNull() {
        assertNull(maritalStatusConverter.convertToEntityAttribute(null));
    }

    @Test
    public void convertToEntityAttribute_shouldThrowExceptionForInvalidDatabaseColumn() {
        assertThrows(IllegalStateException.class, () -> maritalStatusConverter.convertToEntityAttribute('X'));
    }
}
