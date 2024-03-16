package com.cdx.bas.domain.money;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AmountUtilsTest {

    @Test
    public void shouldReturnTrue_whenPositiveValue() {
        assertThat(AmountUtils.isNotPositive(new BigDecimal("1"))).isFalse();
    }

    @Test
    public void shouldReturnFalse_whenZeroValue() {
        assertThat(AmountUtils.isNotPositive(new BigDecimal("0"))).isTrue();
    }

    @Test
    public void shouldReturnFalse_whenNegativeValue() {
        assertThat(AmountUtils.isNotPositive(new BigDecimal("-1"))).isTrue();
    }
}