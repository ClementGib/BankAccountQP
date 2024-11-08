package com.cdx.bas.application.bank.customer.gender;

import org.junit.jupiter.api.Test;

import static com.cdx.bas.domain.bank.customer.gender.Gender.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenderConverterTest {

    private final GenderConverter genderConverter = new GenderConverter();

    @Test
    void convertToDatabaseColumn_shouldConvertToGenderCode() {
        assertThat(genderConverter.convertToDatabaseColumn(MALE)).isEqualTo(strMale);
        assertThat(genderConverter.convertToDatabaseColumn(FEMALE)).isEqualTo(strFemale);
        assertThat(genderConverter.convertToDatabaseColumn(OTHER)).isEqualTo(strOther);
    }

    @Test
    void convertToDatabaseColumn_shouldReturnNull_whenGenderIsNull() {
        assertThat(genderConverter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_shouldConvertToGender() {
        assertThat(genderConverter.convertToEntityAttribute(strMale)).isEqualTo(MALE);
        assertThat(genderConverter.convertToEntityAttribute(strFemale)).isEqualTo(FEMALE);
        assertThat(genderConverter.convertToEntityAttribute(strOther)).isEqualTo(OTHER);
    }

    @Test
    void convertToEntityAttribute_shouldReturnNull_whenGenderCodeIsNull() {
        assertThat(genderConverter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_shouldThrowExceptionForInvalidDatabaseColumn() {
        assertThrows(IllegalStateException.class, () -> genderConverter.convertToEntityAttribute('X'));
    }
}
