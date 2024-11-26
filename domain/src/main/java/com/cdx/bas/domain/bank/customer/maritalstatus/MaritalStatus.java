package com.cdx.bas.domain.bank.customer.maritalstatus;

public enum MaritalStatus {
    SINGLE('S'),
    MARRIED('M'),
    WIDOWED('W'),
    DIVORCED('D'),
    PACS('P');

    public static final char STR_SINGLE = 'S';
    public static final char STR_MARRIED = 'M';
    public static final char STR_WIDOWED = 'W';
    public static final char STR_DIVORCED = 'D';
    public static final char STR_PACS = 'P';

    private final char maritalCode;

    MaritalStatus(Character maritalCode) {
        this.maritalCode = maritalCode;
    }

    public char getMaritalCode() {
        return maritalCode;
    }
}
