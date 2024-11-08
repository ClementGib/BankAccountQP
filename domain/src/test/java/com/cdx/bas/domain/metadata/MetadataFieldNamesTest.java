package com.cdx.bas.domain.metadata;

import com.cdx.bas.domain.money.AmountUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class MetadataFieldNamesTest {
    @Test
    void shouldThrowException_whenAttemptingToInstantiate() throws NoSuchMethodException {
        Constructor<AmountUtils> constructor = AmountUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }
}