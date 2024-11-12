package com.cdx.bas.domain.bank.customer.gender;

public enum Gender {
    MALE('M'),
    FEMALE('F'),
    OTHER('O');

    public static final char STR_MALE = 'M';
    public static final char STR_FEMALE = 'F';
    public static final char STR_OTHER = 'O';
    private final char genderCode;

    Gender(Character maritalCode) {
        this.genderCode = maritalCode;
    }

    public char getGenderCode() {
        return genderCode;
    }
}
