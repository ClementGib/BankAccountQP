package com.cdx.bas.application.bank.customer.gender;

import org.junit.jupiter.api.Test;

import static com.cdx.bas.domain.bank.customer.gender.Gender.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenderConverterTest {

    private final GenderConverter genderConverter = new GenderConverter();

    @Test
    void convertToDatabaseColumn_shouldConvertToGenderCode() {
        assertThat(genderConverter.convertToDatabaseColumn(MALE)).isEqualTo(STR_MALE);
        assertThat(genderConverter.convertToDatabaseColumn(FEMALE)).isEqualTo(STR_FEMALE);
        assertThat(genderConverter.convertToDatabaseColumn(OTHER)).isEqualTo(STR_OTHER);
    }

    @Test
    void convertToDatabaseColumn_shouldReturnNull_whenGenderIsNull() {
        assertThat(genderConverter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void convertToEntityAttribute_shouldConvertToGender() {
        assertThat(genderConverter.convertToEntityAttribute(STR_MALE)).isEqualTo(MALE);
        assertThat(genderConverter.convertToEntityAttribute(STR_FEMALE)).isEqualTo(FEMALE);
        assertThat(genderConverter.convertToEntityAttribute(STR_OTHER)).isEqualTo(OTHER);
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
