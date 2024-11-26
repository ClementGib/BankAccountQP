package com.cdx.bas.application.bank.customer.gender;

import com.cdx.bas.domain.bank.customer.gender.Gender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import static com.cdx.bas.domain.bank.customer.gender.Gender.*;

@Converter
public class GenderConverter implements AttributeConverter<Gender, Character> {

    static final String ERROR_GENDER = "Unexpected gender value: ";

    @Override
    public Character convertToDatabaseColumn(Gender gender) {
        if (gender == null) {
            return null;
        }

        return switch (gender) {
            case MALE -> STR_MALE;
            case FEMALE -> STR_FEMALE;
            case OTHER -> STR_OTHER;
        };
    }

    @Override
    public Gender convertToEntityAttribute(Character genderCode) {
        if (genderCode == null) {
            return null;
        }

        return switch (genderCode) {
            case STR_MALE -> MALE;
            case STR_FEMALE -> FEMALE;
            case STR_OTHER -> OTHER;
            default -> throw new IllegalStateException(ERROR_GENDER + genderCode);
        };
    }
}
