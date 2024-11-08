package com.cdx.bas.domain.money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyTest {

    @Test
    void Money_shouldInstantiateMoneyWithAmountValue_whenAmountValueIsAPositiveNumber() {
        Money money = new Money(new BigDecimal("100"));

        assertThat(money.getAmount()).isEqualTo("100");
    }

    @Test
    void Money_shouldInstantiateMoneyWithAmountValue_whenAmountValueIsANegativeNumber() {
        double amount = -100.00;
        Money money = new Money(new BigDecimal(amount));

        assertThat(money.getAmount()).isEqualTo("-100");
    }
    
    @Test
    void of_shouldReturnMoneyWithAmountOfValue_whenValueIsAPositiveNumber() {
        double amount = 100.00;
        BigDecimal value = new BigDecimal(amount);

        Money money = Money.of(value);

        assertThat(money.getAmount()).isEqualTo(new BigDecimal("100"));
    }
    
    @Test
    void of_shouldReturnMoneyWithAmountOfValue_whenValueIsANegativeNumber() {
        double amount = -100.00;
        BigDecimal value = new BigDecimal(amount);

        Money money = Money.of(value);

        assertThat(money.getAmount()).isEqualTo(new BigDecimal("-100"));
    }

    @Test
    void minus_shouldSubtractAmount_whenAmountIsPositiveAndValueIsPositive() {
        Money moneyOne = new Money(new BigDecimal("100"));
        Money moneyTwo = new Money(new BigDecimal("100"));

        moneyOne.minus(moneyTwo);
        assertThat(moneyOne.getAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void minus_shouldSubtractAmount_whenAmountIsZeroAndValueIsPositive() {
        Money moneyOne = new Money(new BigDecimal("0"));
        Money moneyTwo = new Money(new BigDecimal("100"));

        moneyOne.minus(moneyTwo);

        assertThat(moneyOne.getAmount()).isEqualTo(new BigDecimal("-100"));
    }

    @Test
    void minus_shouldAdditionAmount_whenValuesIsNegative() {
        Money moneyOne = new Money(new BigDecimal("-100"));
        Money moneyTwo = new Money(new BigDecimal("-100"));

        moneyOne.minus(moneyTwo);

        assertThat(moneyOne.getAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void plus_shouldAdditionAmount_whenAmountIsPositiveAndValueIsPositive() {
        Money moneyOne = new Money(new BigDecimal("100"));
        Money moneyTwo = new Money(new BigDecimal("100"));

        moneyOne.plus(moneyTwo);

        assertThat(moneyOne.getAmount()).isEqualTo(new BigDecimal("200"));
    }

    @Test
    void plus_shouldAdditionAmount_whenAmountIsZeroAndValueIsPositive() {
        Money moneyOne = new Money(new BigDecimal("0"));
        Money moneyTwo = new Money(new BigDecimal("100"));

        moneyOne.plus(moneyTwo);

        assertThat(moneyOne.getAmount()).isEqualTo(new BigDecimal("100"));
    }

    @Test
    void plus_shouldAdditionAmount_whenValuesIsNegative() {
        Money moneyOne = new Money(new BigDecimal("-100"));
        Money moneyTwo = new Money(new BigDecimal("-100"));

        moneyOne.plus(moneyTwo);

        assertThat(moneyOne.getAmount()).isEqualTo(new BigDecimal("-200"));
    }

    @Test
    void isPositive_shouldReturnTrue_whenAmountValueIsGreaterToZero() {
        Money money = new Money(new BigDecimal("100"));
        assertThat(money.isPositive()).isTrue();
    }

    @Test
    void isPositive_shouldReturnFalse_whenAmountValueIsLessToZero() {
        Money money = new Money(new BigDecimal("-100"));

        money.isPositive();

        assertThat(money.isPositive()).isFalse();
    }

    @Test
    void isPositiveOrZero_shouldReturnTrue_whenAmountValueIsGreaterToZero() {
        Money money = new Money(new BigDecimal("100"));

        assertThat(money.isPositiveOrZero()).isTrue();
    }

    @Test
    void isPositiveOrZero_shouldReturnTrue_whenAmountValueIsEqualToZero() {
        Money money = new Money(new BigDecimal("0"));

        assertThat(money.isPositiveOrZero()).isTrue();
    }

    @Test
    void isPositiveOrZero_shouldReturnFalse_whenAmountValueIsLessToZero() {
        Money money = new Money(new BigDecimal("-100"));

        money.isPositive();

        assertThat(money.isPositiveOrZero()).isFalse();
    }

    @Test
    void isNegative_shouldReturnTrue_whenAmountValueIsLessToZero() {
        Money money = new Money(new BigDecimal("-100"));

        assertThat(money.isNegative()).isTrue();
    }

    @Test
    void isNegative_shouldReturnFalse_whenAmountValueIsGreaterToZero() {
        Money money = new Money(new BigDecimal("100"));

        assertThat(money.isNegative()).isFalse();
    }

    @Test
    void isGreaterThan_shouldReturnTrue_whenAmountValueIsGreaterThanAmountParameter() {
        Money moneyOne = new Money(new BigDecimal("100"));
        Money moneyTwo = new Money(new BigDecimal("50"));

        assertThat(moneyOne.isGreaterThan(moneyTwo)).isTrue();
    }

    @Test
    void isGreaterThan_shouldReturnFalse_whenAmountValueIsLessThanAmountParameter() {
        Money moneyOne = new Money(new BigDecimal("50"));
        Money moneyTwo = new Money(new BigDecimal("100"));

        assertThat(moneyOne.isGreaterThan(moneyTwo)).isFalse();
    }

    @Test
    void isGreaterThan_shouldReturnFalse_whenAmountValueIsEqualToAmountParameter() {
        Money moneyOne = new Money(new BigDecimal("100"));
        Money moneyTwo = new Money(new BigDecimal("100"));

        assertThat(moneyOne.isGreaterThan(moneyTwo)).isFalse();
    }

    @Test
    void isGreaterThanOrEqual_shouldReturnTrue_whenAmountValueIsGreaterThanAmountParameter() {
        Money moneyOne = new Money(new BigDecimal("100"));
        Money moneyTwo = new Money(new BigDecimal("50"));

        assertThat(moneyOne.isGreaterThanOrEqual(moneyTwo)).isTrue();
    }

    @Test
    void isGreaterThanOrEqual_shouldReturnFalse_whenAmountValueIsLessThanAmountParameter() {
        Money moneyOne = new Money(new BigDecimal("50"));
        Money moneyTwo = new Money(new BigDecimal("100"));

        assertThat(moneyOne.isGreaterThanOrEqual(moneyTwo)).isFalse();
    }

    @Test
    void isGreaterThanOrEqual_shouldReturnTrue_whenAmountValueIsEqualToAmountParameter() {
        Money moneyOne = new Money(new BigDecimal("100"));
        Money moneyTwo = new Money(new BigDecimal("100"));

        assertThat(moneyOne.isGreaterThanOrEqual(moneyTwo)).isTrue();
    }
}