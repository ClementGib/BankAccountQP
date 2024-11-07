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
        return switch (maritalStatus) {
            case SINGLE -> strSingle;
            case MARRIED -> strMarried;
            case WIDOWED -> strWidowed;
            case DIVORCED -> strDivorced;
            case PACS -> strPacs;
        };
    }

    @Override
    public MaritalStatus convertToEntityAttribute(Character maritalCode) {
        return switch (maritalCode) {
            case strSingle -> SINGLE;
            case strMarried -> MARRIED;
            case strWidowed -> WIDOWED;
            case strDivorced -> DIVORCED;
            case strPacs -> PACS;
            default -> throw new IllegalStateException(ERROR_MARITAL_STATUS + maritalCode);
        };
    }
}
