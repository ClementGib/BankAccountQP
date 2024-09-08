package com.cdx.bas.domain.money;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class AmountUtils {
    public static boolean isNotPositive(BigDecimal euroAmount) {
        return euroAmount.signum() <= 0;
    }
}
