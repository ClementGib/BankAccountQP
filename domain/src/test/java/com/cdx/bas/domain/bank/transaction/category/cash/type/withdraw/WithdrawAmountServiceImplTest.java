package com.cdx.bas.domain.bank.transaction.category.cash.type.withdraw;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.saving.SavingBankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.category.cash.CashTransactionProcessingDetails;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.money.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class WithdrawAmountServiceImplTest {

    private final WithdrawAmountServiceImpl withdrawAmountService = new WithdrawAmountServiceImpl();

    @Test
    void shouldApplyToAccount_whenWithdrawProcessorWithPositiveAmount() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setCurrency("EUR");
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setEmitterAccountId(1L);
        transaction.setAmount(new BigDecimal("100.00"));

        BankAccount emitterBankAccount = new SavingBankAccount();
        emitterBankAccount.setId(1L);
        emitterBankAccount.setBalance(Money.of(new BigDecimal("100.00")));
        CashTransactionProcessingDetails cashTransactionProcessingDetails = new CashTransactionProcessingDetails(transaction, emitterBankAccount, new HashMap<>());

        // Act
        withdrawAmountService.applyToAccount(cashTransactionProcessingDetails);

        // Assert
        assertThat(emitterBankAccount.getBalance())
                .usingRecursiveComparison()
                .isEqualTo(Money.of(new BigDecimal("0.00")));
    }

    @Test
    void shouldThrowTransactionException_whenWithdrawProcessorWithNegativeAmount() {
        // Arrange
        Transaction transaction = new Transaction();
        long transactionId = 1L;
        transaction.setId(transactionId);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setCurrency("EUR");
        transaction.setEmitterAccountId(transactionId);
        transaction.setAmount(new BigDecimal("-100.0"));

        BankAccount emitterBankAccount = new SavingBankAccount();
        emitterBankAccount.setId(1L);
        emitterBankAccount.setBalance(Money.of(new BigDecimal("100.0")));
        CashTransactionProcessingDetails cashTransactionProcessingDetails = new CashTransactionProcessingDetails(transaction, emitterBankAccount, new HashMap<>());

        // Act
        try {
            withdrawAmountService.applyToAccount(cashTransactionProcessingDetails);
            fail("should have a positive value or fail");
        } catch (TransactionException exception) {
            // Assert
            assertThat(exception.getMessage()).contains("Withdraw transaction: withdraw failed - should have positive value\nTransaction id:"+ transactionId
                    + "\nEuro amount:" + transaction.getAmount());
        }
    }
}