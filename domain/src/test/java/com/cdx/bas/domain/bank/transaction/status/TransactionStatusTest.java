package com.cdx.bas.domain.bank.transaction.status;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TransactionStatusTest {

    @Test
    void fromString_shouldReturnEnumValue_whenValidStringIsProvided() {
        assertThat(TransactionStatus.fromString("UNPROCESSED")).isEqualTo(TransactionStatus.UNPROCESSED);
        assertThat(TransactionStatus.fromString("outstanding")).isEqualTo(TransactionStatus.OUTSTANDING);
        assertThat(TransactionStatus.fromString("waiting")).isEqualTo(TransactionStatus.WAITING);
        assertThat(TransactionStatus.fromString("completed")).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(TransactionStatus.fromString("refused")).isEqualTo(TransactionStatus.REFUSED);
        assertThat(TransactionStatus.fromString("error")).isEqualTo(TransactionStatus.ERROR);
    }

    @Test
    void fromString_shouldThrowException_whenInvalidStringIsProvided() {
        assertThatThrownBy(() -> TransactionStatus.fromString("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid status: INVALID");
    }

    @Test
    void fromString_shouldThrowException_whenNullStringIsProvided() {
        assertThatThrownBy(() -> TransactionStatus.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid status: null");
    }
}
