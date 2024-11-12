
package com.cdx.bas.domain.bank.transaction.category.digital.type.credit;

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

class CreditAmountServiceImplTest {

    private final CreditAmountServiceImpl creditAmountService = new CreditAmountServiceImpl();

    @Test
    void shouldTransferBetweenAccounts_whenCreditProcessorWithPositiveAmount() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setCurrency("EUR");
        transaction.setType(TransactionType.CREDIT);
        transaction.setEmitterAccountId(1L);
        transaction.setReceiverAccountId(2L);
        transaction.setAmount(new BigDecimal("100"));

        BankAccount emitterBankAccount = new SavingBankAccount();
        emitterBankAccount.setId(1L);
        emitterBankAccount.setBalance(Money.of(new BigDecimal("100.0")));

        BankAccount receiverBankAccount = new SavingBankAccount();
        receiverBankAccount.setId(2L);
        receiverBankAccount.setBalance(Money.of(new BigDecimal("0.0")));
        DigitalTransactionProcessingDetails digitalTransactionProcessingDetails = new DigitalTransactionProcessingDetails(transaction, emitterBankAccount, receiverBankAccount, new HashMap<>());

        // Act
        creditAmountService.transferBetweenAccounts(digitalTransactionProcessingDetails);

        // Assert
        assertThat(emitterBankAccount.getBalance())
                .usingRecursiveComparison()
                .isEqualTo(Money.of(new BigDecimal("0.0")));
        assertThat(receiverBankAccount.getBalance())
                .usingRecursiveComparison()
                .isEqualTo(Money.of(new BigDecimal("100.0")));
    }

    @Test
    void shouldThrowTransactionException_whenCreditProcessorWithNegativeAmount() {
        // Arrange
        Transaction transaction = new Transaction();
        long transactionId = 1L;
        transaction.setId(transactionId);
        transaction.setType(TransactionType.CREDIT);
        transaction.setCurrency("EUR");
        transaction.setEmitterAccountId(transactionId);
        transaction.setReceiverAccountId(2L);
        transaction.setAmount(new BigDecimal("-100.0"));

        BankAccount emitterBankAccount = new SavingBankAccount();
        emitterBankAccount.setId(1L);
        emitterBankAccount.setBalance(Money.of(new BigDecimal("100.0")));

        BankAccount receiverBankAccount = new SavingBankAccount();
        receiverBankAccount.setId(2L);
        receiverBankAccount.setBalance(Money.of(new BigDecimal("0.0")));
        DigitalTransactionProcessingDetails digitalTransactionProcessingDetails = new DigitalTransactionProcessingDetails(transaction, emitterBankAccount, receiverBankAccount, new HashMap<>());

        // Act
        try {
            creditAmountService.transferBetweenAccounts(digitalTransactionProcessingDetails);
            fail("should have a positive value or fail");
        } catch (TransactionException exception) {
            // Assert
            assertThat(exception.getMessage()).contains("Credit transaction: credit failed - should have positive value\nTransaction id:"+ transactionId
                    + "\nEuro amount:" + transaction.getAmount());
        }
    }
}