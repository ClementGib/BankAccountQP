package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.bank.transaction.type.TransactionType;
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
        Transaction transaction = Transaction.builder()
                .id(9L)
                .emitterAccountId(8L)
                .receiverAccountId(7L)
                .type(TransactionType.DEBIT)
                .amount(new BigDecimal("5000.00"))
                .currency("EUR")
                .status(TransactionStatus.UNPROCESSED)
                .date(OffsetDateTime.parse("2024-12-06T19:00:10+01:00").toInstant())
                .label("transaction 9")
                .metadata(Map.of())
                .build();

        Transaction expectedTransaction = Transaction.builder()
                .id(9L)
                .emitterAccountId(8L)
                .receiverAccountId(7L)
                .type(TransactionType.DEBIT)
                .amount(new BigDecimal("5000.00"))
                .currency("EUR")
                .status(TransactionStatus.OUTSTANDING)
                .date(OffsetDateTime.parse("2024-12-06T19:00:10+01:00").toInstant())
                .label("transaction 9")
                .metadata(Map.of())
                .build();

        Transaction actualTransaction = transactionStatusServicePort.setAsOutstanding(transaction);
        assertThat(actualTransaction)
                .usingRecursiveComparison()
                .isEqualTo(expectedTransaction);
    }

    @Test
    @Transactional
    void setAsOutstanding_shouldThrowTransactionException_whenStatusIsNotOutstanding() {
        Transaction transaction = Transaction.builder()
                .status(ERROR)
                .build();

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
        Transaction transaction = Transaction.builder()
                .id(7L)
                .emitterAccountId(3L)
                .receiverAccountId(1L)
                .type(TransactionType.CREDIT)
                .amount(new BigDecimal("1000.00"))
                .currency("EUR")
                .status(TransactionStatus.UNPROCESSED)
                .date(OffsetDateTime.parse("2024-12-06T18:00:00+01:00").toInstant()) // Convert OffsetDateTime to Instant
                .label("transaction 7")
                .metadata(Map.of())
                .build();
        Map<String, String> metadata = Map.of("error", "Transaction 1 deposit error for amount 100: error");

        // Act
        Transaction actualTransaction = transactionStatusServicePort.saveStatus(transaction, ERROR, metadata);

        // Assert
        Transaction expectedTransaction = Transaction.builder()
                .id(7L)
                .emitterAccountId(3L)
                .receiverAccountId(1L)
                .type(TransactionType.CREDIT)
                .amount(new BigDecimal("1000.00"))
                .currency("EUR")
                .status(TransactionStatus.ERROR)
                .date(OffsetDateTime.parse("2024-12-06T18:00:00+01:00").toInstant()) // Convert OffsetDateTime to Instant
                .label("transaction 7")
                .metadata(metadata)
                .build();

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