package com.cdx.bas.domain.currency.rate;

import com.cdx.bas.domain.currency.error.CurrencyException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ExchangeRateUtils {
    public static final String PIVOT_CURRENCY = "EUR";
    private static final Double NO_EXCHANGE_RATE_VALUE = 1.0;

    /**
     * Euro Exchange Rates for each currency
     *
     * @param currency key to find rate
     * @return euro exchange rate corresponding to the currency
     */
    private static Double getEuroExchangeRate(String currency) throws CurrencyException {
        if (currencyEuroExchangeRates.containsKey(currency)) {
            return currencyEuroExchangeRates.get(currency);
        } else if (PIVOT_CURRENCY.equals(currency)) {
            return NO_EXCHANGE_RATE_VALUE;
        } else {
            throw new CurrencyException("No exchange rate found for currency: " + currency);
        }
    }

    /**
     * Extract amount from Transaction after exchange rate from Transaction currency applied
     *
     * @param currency of the amount
     * @param amount to convert with the exchange rate
     * @return euro amount
     */
    public static BigDecimal getEuroAmountFrom(String currency, BigDecimal amount) throws CurrencyException {
        double value = amount.doubleValue() * getEuroExchangeRate(currency);
        return new BigDecimal(String.valueOf(value));
    }


    /**
     * check if the currency is present in exchange rate Map
     *
     * @param currency to check if present or not
     * @return boolean of presence of the currency
     */
    public static boolean hasCurrency(String currency) {
        return PIVOT_CURRENCY.equals(currency) || ExchangeRateUtils.currencyEuroExchangeRates.containsKey(currency);
    }

    /**
     * Map currency exchange rates for pivot currency EURO
     */
    private static final Map<String, Double> currencyEuroExchangeRates = new HashMap<String, Double>() {
        {
            put("USD", 1.0745);
            put("JPY", 158.18);
            put("BGN", 1.9558);
            put("CZK", 24.217);
            put("DKK", 7.4563);
            put("GBP", 0.85503);
            put("HUF", 389.15);
            put("PLN", 4.501);
            put("RON", 4.9603);
            put("SEK", 11.9105);
            put("CHF", 0.9561);
            put("ISK", 143.9);
            put("NOK", 11.492);
            put("TRY", 28.772);
            put("AUD", 1.6801);
            put("BRL", 5.3502);
            put("CAD", 1.4659);
            put("CNY", 7.8476);
            put("HKD", 8.4261);
            put("IDR", 16431.79);
            put("ILS", 4.0902);
            put("INR", 89.341);
            put("KRW", 1430.83);
            put("MXN", 18.8455);
            put("MYR", 5.0222);
            put("NZD", 1.8224);
            put("PHP", 61.257);
            put("SGD", 1.4624);
            put("THB", 38.166);
            put("ZAR", 20.6702);
        }
    };
}
