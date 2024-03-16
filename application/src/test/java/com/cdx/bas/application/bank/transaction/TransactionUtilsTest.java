package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.NewCashTransaction;
import com.cdx.bas.domain.bank.transaction.NewDigitalTransaction;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.type.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static com.cdx.bas.domain.bank.transaction.type.TransactionType.CREDIT;
import static com.cdx.bas.domain.bank.transaction.type.TransactionType.DEBIT;
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
        Transaction expectedDigitalTransaction = Transaction.builder()
                .emitterAccountId(1L)
                .receiverAccountId(2L)
                .amount(new BigDecimal("100"))
                .currency("USD")
                .type(CREDIT)
                .status(UNPROCESSED)
                .label("credit transaction")
                .metadata(new HashMap<>())
                .build();
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
        Transaction expectedCashTransaction = Transaction.builder()
                .emitterAccountId(1L)
                .amount(new BigDecimal("100"))
                .currency("USD")
                .status(UNPROCESSED)
                .metadata(new HashMap<>())
                .build();
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
        Transaction oldTransaction = Transaction.builder()
                .id(1L)
                .amount(new BigDecimal(100))
                .emitterAccountId(10L)
                .receiverAccountId(11L)
                .type(CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now())
                .label("old transaction")
                .build();

        Instant dateAfter = Instant.now();
        BigDecimal bigDecimalAfter = new BigDecimal(200);
        String labelAfter = "new transaction";
        Transaction newTransaction = Transaction.builder()
                .id(2L)
                .amount(bigDecimalAfter)
                .emitterAccountId(20L)
                .receiverAccountId(22L)
                .type(DEBIT)
                .status(UNPROCESSED)
                .date(dateAfter)
                .label(labelAfter)
                .build();

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

    @Test
    public void test() {
        Transaction t1 = Transaction.builder().build();
        Transaction t2 = Transaction.builder().build();

        Set transactions = new HashSet<Transaction>();

        transactions.add(t1);
        transactions.add(t2);

        System.out.println(transactions);
    }

}