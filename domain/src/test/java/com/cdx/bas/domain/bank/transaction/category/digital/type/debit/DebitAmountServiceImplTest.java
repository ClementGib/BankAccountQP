package com.cdx.bas.domain.bank.transaction.category.digital.type.debit;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.saving.SavingBankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.category.digital.DigitalTransactionProcessingDetails;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.money.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class DebitAmountServiceImplTest {

    private final DebitAmountServiceImpl debitAmountService = new DebitAmountServiceImpl();

    @Test
    void shouldTransferBetweenAccounts_whenDebitProcessorWithPositiveAmount() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setCurrency("EUR");
        transaction.setType(TransactionType.DEBIT);
        transaction.setEmitterAccountId(1L);
        transaction.setReceiverAccountId(2L);
        transaction.setAmount(new BigDecimal("100.0"));
        transaction.setId(1L);

        BankAccount emitterBankAccount = new SavingBankAccount();
        emitterBankAccount.setId(1L);
        emitterBankAccount.setBalance(Money.of(new BigDecimal("0.0")));

        BankAccount receiverBankAccount = new SavingBankAccount();
        receiverBankAccount.setId(2L);
        receiverBankAccount.setBalance(Money.of(new BigDecimal("100.0")));
        DigitalTransactionProcessingDetails digitalTransactionProcessingDetails = new DigitalTransactionProcessingDetails(transaction, emitterBankAccount, receiverBankAccount, new HashMap<>());

        // Act
        debitAmountService.transferBetweenAccounts(digitalTransactionProcessingDetails);

        // Assert
        assertThat(emitterBankAccount.getBalance())
                .usingRecursiveComparison()
                .isEqualTo(Money.of(new BigDecimal("100.0")));
        assertThat(receiverBankAccount.getBalance())
                .usingRecursiveComparison()
                .isEqualTo(Money.of(new BigDecimal("0.0")));
    }

    @Test
    void shouldThrowTransactionException_whenDebitProcessorWithNegativeAmount() {
        // Arrange
        Transaction transaction = new Transaction();
        long transactionId = 1L;
        transaction.setId(transactionId);
        transaction.setCurrency("EUR");
        transaction.setType(TransactionType.DEBIT);
        transaction.setEmitterAccountId(transactionId);
        transaction.setReceiverAccountId(2L);
        transaction.setAmount(new BigDecimal("-100.0"));

        BankAccount emitterBankAccount = new SavingBankAccount();
        emitterBankAccount.setId(1L);
        emitterBankAccount.setBalance(Money.of(new BigDecimal("0.0")));

        BankAccount receiverBankAccount = new SavingBankAccount();
        receiverBankAccount.setId(2L);
        receiverBankAccount.setBalance(Money.of(new BigDecimal("100.0")));
        DigitalTransactionProcessingDetails digitalTransactionProcessingDetails = new DigitalTransactionProcessingDetails(transaction, emitterBankAccount, receiverBankAccount, new HashMap<>());

        // Act
        try {
            debitAmountService.transferBetweenAccounts(digitalTransactionProcessingDetails);
            fail("should have a positive value or fail");
        } catch (TransactionException exception) {
            // Assert
            assertThat(exception.getMessage()).contains("Debit transaction: debit failed - should have positive value\nTransaction id:"+ transactionId
                    + "\nEuro amount:" + transaction.getAmount());
        }
    }
}