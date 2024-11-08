package com.cdx.bas.domain.money;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AmountUtilsTest {

    @Test
    void shouldReturnTrue_whenPositiveValue() {
        assertThat(AmountUtils.isNotPositive(new BigDecimal("1"))).isFalse();
    }

    @Test
    void shouldReturnFalse_whenZeroValue() {
        assertThat(AmountUtils.isNotPositive(new BigDecimal("0"))).isTrue();
    }

    @Test
    void shouldReturnFalse_whenNegativeValue() {
        assertThat(AmountUtils.isNotPositive(new BigDecimal("-1"))).isTrue();
    }

    @Test
    void shouldThrowException_whenAttemptingToInstantiate() throws NoSuchMethodException {
        Constructor<AmountUtils> constructor = AmountUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }
}