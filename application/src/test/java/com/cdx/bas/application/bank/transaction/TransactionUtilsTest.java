package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.category.NewCashTransaction;
import com.cdx.bas.domain.bank.transaction.category.NewDigitalTransaction;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType.DEBIT;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TransactionUtilsTest {

    @Test
    void should_getNewDigitalTransaction_when_hasTransactioNewDigitalTransaction() {
        // Arrange
        Instant before = Instant.now();
        Long emitterAccountId = 1L;
        Long receiverAccountId = 2L;
        BigDecimal amount = new BigDecimal("100");
        String currency = "USD";
        TransactionType type = TransactionType.CREDIT;
        String label = "credit transaction";
        Map<String, String> metadata = new HashMap<>();
        NewDigitalTransaction newDigitalTransaction = new NewDigitalTransaction(emitterAccountId, receiverAccountId, amount, currency, type, label, metadata);

        // Act
        Transaction actualDigitalTransaction = TransactionUtils.getNewDigitalTransaction(newDigitalTransaction);

        // Assert
        Transaction expectedDigitalTransaction = new Transaction();
        expectedDigitalTransaction.setEmitterAccountId(1L);
        expectedDigitalTransaction.setReceiverAccountId(2L);
        expectedDigitalTransaction.setAmount(new BigDecimal("100"));
        expectedDigitalTransaction.setCurrency("USD");
        expectedDigitalTransaction.setType(TransactionType.CREDIT);
        expectedDigitalTransaction.setStatus(TransactionStatus.UNPROCESSED);
        expectedDigitalTransaction.setLabel("credit transaction");
        expectedDigitalTransaction.setMetadata(new HashMap<>());

        assertThat(actualDigitalTransaction)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(Instant.class)
                .isEqualTo(expectedDigitalTransaction);
        Instant after = Instant.now();
        assertThat(actualDigitalTransaction.getDate()).isAfter(before);
        assertThat(actualDigitalTransaction.getDate()).isBefore(after);
    }

    @Test
    void should_getNewCashTransaction_when_hasTransactioNewDigitalTransaction() {
        // Arrange
        Instant before = Instant.now();
        Long emitterAccountId = 1L;
        BigDecimal amount = new BigDecimal("100");
        String currency = "USD";
        Map<String, String> metadata = new HashMap<>();
        NewCashTransaction newDigitalTransaction = new NewCashTransaction(emitterAccountId, amount, currency, metadata);

        // Act
        Transaction actualCashTransaction = TransactionUtils.getNewCashTransaction(newDigitalTransaction);

        // Assert
        Transaction expectedCashTransaction = new Transaction();
        expectedCashTransaction.setEmitterAccountId(1L);
        expectedCashTransaction.setAmount(new BigDecimal("100"));
        expectedCashTransaction.setCurrency("USD");
        expectedCashTransaction.setStatus(TransactionStatus.UNPROCESSED);
        expectedCashTransaction.setMetadata(new HashMap<>());
        assertThat(actualCashTransaction)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(Instant.class)
                .isEqualTo(expectedCashTransaction);
        Instant after = Instant.now();
        assertThat(actualCashTransaction.getDate()).isAfter(before);
        assertThat(actualCashTransaction.getDate()).isBefore(after);
    }

    @Test
    void shouldMergeOldTransactionWithNewTransaction_whenOldTransactionAndNewTransactionAreValid() {
        // Arrange
        Transaction oldTransaction = new Transaction();
        oldTransaction.setId(1L);
        oldTransaction.setAmount(new BigDecimal(100));
        oldTransaction.setEmitterAccountId(10L);
        oldTransaction.setReceiverAccountId(11L);
        oldTransaction.setType(TransactionType.CREDIT);
        oldTransaction.setStatus(TransactionStatus.UNPROCESSED);
        oldTransaction.setDate(Instant.now());
        oldTransaction.setLabel("old transaction");

        Instant dateAfter = Instant.now();
        BigDecimal bigDecimalAfter = new BigDecimal(200);
        String labelAfter = "new transaction";
        Transaction newTransaction = new Transaction();
        newTransaction.setId(2L);
        newTransaction.setAmount(bigDecimalAfter);
        newTransaction.setEmitterAccountId(20L);
        newTransaction.setReceiverAccountId(22L);
        newTransaction.setType(TransactionType.DEBIT);
        newTransaction.setStatus(TransactionStatus.UNPROCESSED);
        newTransaction.setDate(dateAfter);
        newTransaction.setLabel(labelAfter);

        // Act
        Transaction actualTransaction = TransactionUtils.mergeTransactions(oldTransaction, newTransaction);

        oldTransaction.setId(2L);
        oldTransaction.setAmount(bigDecimalAfter);
        oldTransaction.setEmitterAccountId(20L);
        oldTransaction.setReceiverAccountId(22L);
        oldTransaction.setType(DEBIT);
        oldTransaction.setStatus(UNPROCESSED);
        oldTransaction.setDate(dateAfter);
        oldTransaction.setLabel(labelAfter);

        // Assert
        assertThat(actualTransaction).isEqualTo(oldTransaction);
    }
}