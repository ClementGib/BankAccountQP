package com.cdx.bas.domain.currency.rate;

import com.cdx.bas.domain.currency.error.CurrencyException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ExchangeRateUtilsTest {

    @Test
    void getEuroAmountFrom_shouldThrowCurrencyException_whenExchangeRateNotFound() {
        String currency = "ABC";
        BigDecimal amount = BigDecimal.valueOf(1000);

        assertThatThrownBy(() -> ExchangeRateUtils.getEuroAmountFrom(currency, amount))
                .isInstanceOf(CurrencyException.class)
                .hasMessage("No exchange rate found for currency: ABC");
    }

    @Test
    void getEuroAmountFrom_shouldReturnSameAmount_whenCurrencyIsEUR() {
        String currency = "EUR";
        BigDecimal amount = BigDecimal.valueOf(1000);

        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(currency, amount);

        assertThat(euroAmount).isEqualTo(BigDecimal.valueOf(1000.0));
    }

    @Test
    void getEuroAmountFrom_shouldReturnConvertedAmount_whenExchangeRateFound() {
        String currency = "JPY";
        BigDecimal amount = BigDecimal.valueOf(1000);

        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(currency, amount);

        assertThat(euroAmount).isEqualTo(BigDecimal.valueOf(158180.0));
    }

    @Test
    void getEuroAmountFrom_shouldReturnZero_whenAmountIsZero() {
        String currency = "USD";
        BigDecimal amount = BigDecimal.ZERO;

        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(currency, amount);

        assertThat(euroAmount).isEqualTo(new BigDecimal("0.0"));
    }

    @Test
    void getEuroAmountFrom_shouldHandleVeryLargeAmount() {
        String currency = "USD";
        BigDecimal amount = BigDecimal.valueOf(1e9);  // Test with a large amount

        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(currency, amount);

        assertThat(euroAmount).isEqualTo(BigDecimal.valueOf(1.0745e9));
    }

    @Test
    void getEuroAmountFrom_shouldHandleVerySmallAmount() {
        String currency = "USD";
        BigDecimal amount = BigDecimal.valueOf(1e-9);  // Test with a small amount

        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(currency, amount);

        assertThat(euroAmount).isEqualTo(new BigDecimal("1.0745000000000002E-9"));
    }

    @Test
    void hasCurrency_shouldReturnTrue_whenCurrencyIsEUR() {
        assertThat(ExchangeRateUtils.hasCurrency("EUR")).isTrue();
    }

    @Test
    void hasCurrency_shouldReturnTrue_whenCurrencyExistsInExchangeRateMap() {
        assertThat(ExchangeRateUtils.hasCurrency("USD")).isTrue();
    }

    @Test
    void hasCurrency_shouldReturnFalse_whenCurrencyDoesNotExist() {
        assertThat(ExchangeRateUtils.hasCurrency("ABC")).isFalse();
    }

    @Test
    void hasCurrency_shouldReturnFalse_whenCurrencyIsNull() {
        assertThat(ExchangeRateUtils.hasCurrency(null)).isFalse();
    }

    @Test
    void getEuroAmountFrom_shouldThrowCurrencyException_whenCurrencyIsNull() {
        BigDecimal amount = BigDecimal.valueOf(1000);

        assertThatThrownBy(() -> ExchangeRateUtils.getEuroAmountFrom(null, amount))
                .isInstanceOf(CurrencyException.class)
                .hasMessage("No exchange rate found for currency: null");
    }
}
