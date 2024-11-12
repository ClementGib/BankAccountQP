package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType.CREDIT;
import static com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType.DEBIT;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static org.assertj.core.api.Assertions.assertThat;


@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@WithTestResource(H2DatabaseTestResource.class)
class TransactionRepositoryTest {

    @Inject
    TransactionRepository transactionRepository;

    @Test
    @Order(1)
    void findById_shouldFindTransaction_whenIdIsFound() {
        Instant expectedInstant = OffsetDateTime.of(
                2024, 6, 6, 12, 0, 0, 0,
                ZoneOffset.ofHours(1)).toInstant();
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setEmitterAccountId(1L);
        transaction.setReceiverAccountId(2L);
        transaction.setAmount(new BigDecimal("1600.00"));
        transaction.setCurrency("EUR");
        transaction.setType(TransactionType.CREDIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDate(expectedInstant);
        transaction.setLabel("transaction 1");
        transaction.setMetadata(Map.of(
                "emitter_amount_before", "2000",
                "receiver_amount_before", "0",
                "emitter_amount_after", "400",
                "receiver_amount_after", "1600"
        ));

        Optional<Transaction> expectedTransaction = Optional.of(transaction);
        Optional<Transaction> actualTransaction = transactionRepository.findById(1);

        // Assert
        assertThat(actualTransaction).isPresent()
                .usingRecursiveComparison()
                .isEqualTo(expectedTransaction);
    }

    @Test
    @Order(2)
    void findUnprocessedTransactions_shouldFindEveryUnprocessedTransactions() {
        Queue<Transaction> actualUnprocessedTransactions = transactionRepository.findUnprocessedTransactions();

        Queue<Transaction> expectedUnprocessedTransactions = new PriorityQueue<>();
        expectedUnprocessedTransactions.add(new Transaction(5L, 2L, 1L, new BigDecimal("600.99"), "EUR", CREDIT, UNPROCESSED, Instant.parse("2024-11-06T17:00:00+00:00"), "transaction 5", new HashMap<>()));
        expectedUnprocessedTransactions.add(new Transaction(6L, 1L, 7L, new BigDecimal("2000.00"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-11-06T17:30:00+00:00"), "transaction 6", new HashMap<>()));
        expectedUnprocessedTransactions.add(new Transaction(7L, 3L, 1L, new BigDecimal("1000.00"), "EUR", CREDIT, UNPROCESSED, Instant.parse("2024-12-06T17:00:00+00:00"), "transaction 7", new HashMap<>()));
        expectedUnprocessedTransactions.add(new Transaction(8L, 4L, 2L, new BigDecimal("300.80"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-12-06T18:00:00+00:00"), "transaction 8", new HashMap<>()));
        expectedUnprocessedTransactions.add(new Transaction(9L, 8L, 7L, new BigDecimal("5000.00"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 9", new HashMap<>()));

        assertThat(actualUnprocessedTransactions)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedUnprocessedTransactions);
    }

    @Test
    @Order(3)
    @Transactional
    void create_shouldPersistTransaction() {
        long id = 20L;
        Transaction transactionToCreate = new Transaction(null, 8L, 1L, new BigDecimal("99999.00"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 8", new HashMap<>());
        transactionRepository.create(transactionToCreate);

        Optional<Transaction> actualOptionalTransaction = transactionRepository.findById(id);

        Transaction expectedTransaction = new Transaction(id, 8L, 1L, new BigDecimal("99999.00"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 8", new HashMap<>());
        assertThat(actualOptionalTransaction).isNotEmpty();
        assertThat(actualOptionalTransaction.get())
                .usingRecursiveComparison()
                .isEqualTo(expectedTransaction);
    }

    @Test
    @Order(4)
    @Transactional
    void update_shouldMergeTransaction() {
        Transaction expectedTransaction = new Transaction(2L, 6L, 3L,
                new BigDecimal("9200.00"), "EUR", CREDIT, UNPROCESSED,
                Instant.parse("2024-11-10T15:00:00+02:00"),
                "transaction to process", Map.of("emitter_amount_before", "9200", "receiver_amount_before", "10000", "emitter_amount_after", "0", "receiver_amount_after", "19200"));
        Optional<Transaction> optionalExpectedTransaction = Optional.of(expectedTransaction);

        Transaction updatedTransaction = transactionRepository.update(expectedTransaction);
        Optional<Transaction> actualOptionalTransaction = transactionRepository.findById(2);

        assertThat(updatedTransaction)
                .usingRecursiveComparison()
                .isEqualTo(expectedTransaction);
        assertThat(actualOptionalTransaction).isPresent()
                .usingRecursiveComparison()
                .isEqualTo(optionalExpectedTransaction);
    }

    @Test
    @Order(5)
    @Transactional
    void deleteById_shouldDeleteTransaction_whenIdIsFound() {
        // Arrange
        long id = 20L;
        Transaction transactionToDelete = new Transaction(id, 8L, 1L, new BigDecimal("99999.00"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 8", new HashMap<>());
        Optional<Transaction> optionalTransaction = Optional.of(transactionToDelete);

        // Act
        Optional<Transaction> deletedTransaction = transactionRepository.deleteById(id);

        // Assert
        Optional<Transaction> deletedOptionalTransaction = transactionRepository.findById(id);

        assertThat(deletedOptionalTransaction).isEmpty();
        assertThat(deletedTransaction)
                .usingRecursiveComparison()
                .isEqualTo(optionalTransaction);
    }
}