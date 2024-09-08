package com.cdx.bas.domain.currency.rate;

import com.cdx.bas.domain.currency.error.CurrencyException;
import com.cdx.bas.domain.currency.rate.ExchangeRateUtils;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class ExchangeRateUtilsTest {

    @Test
    public void getEuroAmountFrom_shouldThrowCurrencyException_whenExchangeRateNotFound() {
        String currency = "ABC";
        BigDecimal amount = new BigDecimal(1000);
        try {
            ExchangeRateUtils.getEuroAmountFrom(currency, amount);
        } catch (CurrencyException exception) {
            assertThat(exception.getMessage()).isEqualTo("No exchange rate found for currency: ABC");
        }
    }

    @Test
    public void getEuroAmountFrom_shouldReturnBigDecimalAmountWithExchangeRateApplied_whenCurrencyIsEUR() {
        String currency = "EUR";
        BigDecimal amount = new BigDecimal(1000);

        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(currency, amount);

        assertThat(euroAmount).isEqualTo(new BigDecimal("1000.0"));
    }

    @Test
    public void getEuroAmountFrom_shouldReturnBigDecimalAmountWithExchangeRateApplied_whenExchangeRateFound() {
        String currency = "JPY";
        BigDecimal amount = new BigDecimal(1000);

        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(currency, amount);

        assertThat(euroAmount).isEqualTo(new BigDecimal("158180.0"));
    }

    @Test
    public void hasCurrency_shouldReturnTrue_whenCurrencyIsEUR() {
        String currency = "EUR";

        boolean hasCurrency = ExchangeRateUtils.hasCurrency(currency);

        assertThat(hasCurrency).isTrue();
    }

    @Test
    public void hasCurrency_shouldReturnTrue_whenCurrencyExistsInExchangeRateMap() {
        String currency = "USD";

        boolean hasCurrency = ExchangeRateUtils.hasCurrency(currency);

        assertThat(hasCurrency).isTrue();
    }

    @Test
    public void hasCurrency_shouldReturnFalse_whenCurrencyIsNotFound() {
        String currency = "ABC";

        boolean hasCurrency = ExchangeRateUtils.hasCurrency(currency);

        assertThat(hasCurrency).isFalse();
    }
}
