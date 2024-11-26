package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.COMPLETED;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.ERROR;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithTestResource(H2DatabaseTestResource.class)
class TransactionStatusServiceTest {

    @Inject
    TransactionStatusServicePort transactionStatusServicePort;

    @Inject
    TransactionRepository transactionPersistencePort;

    @Test
    @Transactional
    void setAsOutstanding_shouldSetStatusAndUpdate_whenStatusIsOutstanding() {
        Transaction transaction = new Transaction();
        transaction.setId(9L);
        transaction.setEmitterAccountId(8L);
        transaction.setReceiverAccountId(7L);
        transaction.setType(TransactionType.DEBIT);
        transaction.setAmount(new BigDecimal("5000.00"));
        transaction.setCurrency("EUR");
        transaction.setStatus(TransactionStatus.UNPROCESSED);
        transaction.setDate(OffsetDateTime.parse("2024-12-06T19:00:10+01:00").toInstant());
        transaction.setLabel("transaction 9");
        transaction.setMetadata(Map.of());

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setId(9L);
        expectedTransaction.setEmitterAccountId(8L);
        expectedTransaction.setReceiverAccountId(7L);
        expectedTransaction.setType(TransactionType.DEBIT);
        expectedTransaction.setAmount(new BigDecimal("5000.00"));
        expectedTransaction.setCurrency("EUR");
        expectedTransaction.setStatus(TransactionStatus.OUTSTANDING);
        expectedTransaction.setDate(OffsetDateTime.parse("2024-12-06T19:00:10+01:00").toInstant());
        expectedTransaction.setLabel("transaction 9");
        expectedTransaction.setMetadata(Map.of());

        Transaction actualTransaction = transactionStatusServicePort.setAsOutstanding(transaction);
        assertThat(actualTransaction)
                .usingRecursiveComparison()
                .isEqualTo(expectedTransaction);
    }

    @Test
    @Transactional
    void setAsOutstanding_shouldThrowTransactionException_whenStatusIsNotOutstanding() {
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.ERROR);

        try {
            transactionStatusServicePort.setAsOutstanding(transaction);
        } catch (TransactionException transactionException) {
            String expectedMessage = "Transaction: set status to outstanding no longer unprocessed\nStatus:ERROR";
            assertThat(transactionException.getMessage()).isEqualTo(expectedMessage);
        }
    }

    @Test
    @Transactional
    void setStatus_shouldSetStatusAndUpdate_whenTransactionIsValid() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId(7L);
        transaction.setEmitterAccountId(3L);
        transaction.setReceiverAccountId(1L);
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setCurrency("EUR");
        transaction.setStatus(TransactionStatus.UNPROCESSED);
        transaction.setDate(OffsetDateTime.parse("2024-12-06T18:00:00+01:00").toInstant()); // Convert OffsetDateTime to Instant
        transaction.setLabel("transaction 7");
        transaction.setMetadata(new HashMap<>()); // or `Map.of()` if an immutable map is fine
        Map<String, String> metadata = Map.of("error", "Transaction 1 deposit error for amount 100: error");

        // Act
        Transaction actualTransaction = transactionStatusServicePort.saveStatus(transaction, ERROR, metadata);

        // Assert
        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setId(7L);
        expectedTransaction.setEmitterAccountId(3L);
        expectedTransaction.setReceiverAccountId(1L);
        expectedTransaction.setType(TransactionType.CREDIT);
        expectedTransaction.setAmount(new BigDecimal("1000.00"));
        expectedTransaction.setCurrency("EUR");
        expectedTransaction.setStatus(TransactionStatus.ERROR);
        expectedTransaction.setDate(OffsetDateTime.parse("2024-12-06T18:00:00+01:00").toInstant()); // Convert OffsetDateTime to Instant
        expectedTransaction.setLabel("transaction 7");
        expectedTransaction.setMetadata(metadata); // Assuming 'metadata' is a Map variable defined elsewhere

        assertThat(actualTransaction)
                .usingRecursiveComparison()
                .isEqualTo(expectedTransaction);
    }

    @Test
    @Transactional
    void setStatus_shouldThrowTransactionException_whenTransactionIsNull() {
        try {
            transactionStatusServicePort.saveStatus(null, COMPLETED, new HashMap<>());
        } catch (TransactionException transactionException) {
            String expectedMessage = "Transaction: set status is null";
            assertThat(transactionException.getMessage()).isEqualTo(expectedMessage);
        }
    }
}