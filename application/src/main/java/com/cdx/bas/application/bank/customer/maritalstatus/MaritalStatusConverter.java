package com.cdx.bas.application.bank.customer.maritalstatus;

import com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import static com.cdx.bas.domain.bank.customer.maritalstatus.MaritalStatus.*;

@Converter
public class MaritalStatusConverter implements AttributeConverter<MaritalStatus, Character> {

    static final String ERROR_MARITAL_STATUS = "Unexpected marital status value: ";

    @Override
    public Character convertToDatabaseColumn(MaritalStatus maritalStatus) {
        if (maritalStatus == null) {
            return null;  // Handle null maritalStatus by returning null
        }
        return switch (maritalStatus) {
            case SINGLE -> STR_SINGLE;
            case MARRIED -> STR_MARRIED;
            case WIDOWED -> STR_WIDOWED;
            case DIVORCED -> STR_DIVORCED;
            case PACS -> STR_PACS;
        };
    }

    @Override
    public MaritalStatus convertToEntityAttribute(Character maritalCode) {
        if (maritalCode == null) {
            return null;  // Handle null maritalCode by returning null
        }
        return switch (maritalCode) {
            case STR_SINGLE -> SINGLE;
            case STR_MARRIED -> MARRIED;
            case STR_WIDOWED -> WIDOWED;
            case STR_DIVORCED -> DIVORCED;
            case STR_PACS -> PACS;
            default -> throw new IllegalStateException(ERROR_MARITAL_STATUS + maritalCode);
        };
    }
}
