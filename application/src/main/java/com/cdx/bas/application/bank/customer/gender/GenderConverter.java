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
        return switch (gender) {
            case MALE -> strMale;
            case FEMALE -> strFemale;
            case OTHER -> strOther;
        };
    }

    @Override
    public Gender convertToEntityAttribute(Character genderCode) {
        return switch (genderCode) {
            case strMale -> MALE;
            case strFemale -> FEMALE;
            case strOther -> OTHER;
            default -> throw new IllegalStateException(ERROR_GENDER + genderCode);
        };
    }
}
