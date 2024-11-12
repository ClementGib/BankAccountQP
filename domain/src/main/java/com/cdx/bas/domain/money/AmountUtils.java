package com.cdx.bas.domain.money;

import java.math.BigDecimal;

public class AmountUtils {

    private AmountUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isNotPositive(BigDecimal euroAmount) {
        return euroAmount.signum() <= 0;
    }
}
