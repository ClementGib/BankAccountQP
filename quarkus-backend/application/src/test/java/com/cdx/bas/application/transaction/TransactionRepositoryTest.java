package com.cdx.bas.application.transaction;

import com.cdx.bas.domain.transaction.Transaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static com.cdx.bas.domain.transaction.TransactionStatus.COMPLETED;
import static com.cdx.bas.domain.transaction.TransactionStatus.UNPROCESSED;
import static com.cdx.bas.domain.transaction.TransactionType.CREDIT;
import static com.cdx.bas.domain.transaction.TransactionType.DEBIT;
import static org.assertj.core.api.Assertions.assertThat;


@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
class TransactionRepositoryTest {

    @Inject
    TransactionRepository transactionRepository;

    @Test
    @Order(1)
    public void findById_should_find_transaction_and_return_it() {
        Optional<Transaction> expectedTransaction = Optional.of(new Transaction(1L, 1L, 2L,
                new BigDecimal("1600.00"), "EUR", CREDIT, COMPLETED,
                OffsetDateTime.parse("2022-06-06T12:00:00+02:00").toInstant(),
                "transaction 1", Map.of("sender_amount_before", "2000", "receiver_amount_before", "0", "sender_amount_after", "400", "receiver_amount_after", "1600")));
        Optional<Transaction> actualTransaction = transactionRepository.findById(1);

        // Assert
        assertThat(actualTransaction).isPresent()
                .usingRecursiveComparison()
                .isEqualTo(expectedTransaction);
    }

    @Test
    @Order(2)
    public void findUnprocessedTransactions_should_find_every_transactions_that_are_not_processed() {
        Queue<Transaction> actualUnprocessedTransactions = transactionRepository.findUnprocessedTransactions();

        Queue<Transaction> expectedUnprocessedTransactions = new PriorityQueue<>();
        expectedUnprocessedTransactions.add(new Transaction(5L, 2L, 1L, new BigDecimal("600.99"), "EUR", CREDIT, UNPROCESSED, OffsetDateTime.parse("2022-11-06T18:00:00+01:00").toInstant(), "transaction 5", new HashMap<>()));
        expectedUnprocessedTransactions.add(new Transaction(6L, 1L, 7L, new BigDecimal("2000.00"), "EUR", DEBIT, UNPROCESSED, OffsetDateTime.parse("2022-11-06T18:30:00+01:00").toInstant(), "transaction 6", new HashMap<>()));
        expectedUnprocessedTransactions.add(new Transaction(7L, 3L, 1L, new BigDecimal("1000.00"), "EUR", CREDIT, UNPROCESSED, OffsetDateTime.parse("2022-12-06T18:00:00+01:00").toInstant(), "transaction 7", new HashMap<>()));
        expectedUnprocessedTransactions.add(new Transaction(8L, 4L, 2L, new BigDecimal("300.80"), "EUR", DEBIT, UNPROCESSED, OffsetDateTime.parse("2022-12-06T19:00:00+01:00").toInstant(), "transaction 8", new HashMap<>()));
        expectedUnprocessedTransactions.add(new Transaction(9L, 8L, 7L, new BigDecimal("5000.00"), "EUR", DEBIT, UNPROCESSED, OffsetDateTime.parse("2022-12-06T19:00:10+01:00").toInstant(), "transaction 9", new HashMap<>()));

        assertThat(actualUnprocessedTransactions)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedUnprocessedTransactions);
    }

    @Test
    @Order(3)
    @Transactional
    public void create_should_persist_transaction_and_return_it() {
        Transaction transactionToCreate = new Transaction(null, 8L, 1L, new BigDecimal("99999.00"), "EUR", DEBIT, UNPROCESSED, OffsetDateTime.parse("2022-12-06T19:00:10+01:00").toInstant(), "transaction 8", new HashMap<>());
        Transaction createdTransaction = transactionRepository.create(transactionToCreate);

        transactionRepository.getEntityManager().flush();
        Optional<Transaction> actualOptionalTransaction = transactionRepository.findById(10L);

        Transaction expectedTransaction = new Transaction(10L, 8L, 1L, new BigDecimal("99999.00"), "EUR", DEBIT, UNPROCESSED, OffsetDateTime.parse("2022-12-06T19:00:10+01:00").toInstant(), "transaction 8", new HashMap<>());
        Optional<Transaction> optionalTransaction = Optional.of(expectedTransaction);
        assertThat(createdTransaction)
                .usingRecursiveComparison()
                .isEqualTo(transactionToCreate);
        assertThat(actualOptionalTransaction)
                .usingRecursiveComparison()
                .isEqualTo(optionalTransaction);
    }

    @Test
    @Order(4)
    @Transactional
    public void update_should_merge_transaction_and_return_it() {
        Transaction expectedTransaction = new Transaction(2L, 6L, 3L,
                new BigDecimal("9200.00"), "EUR", CREDIT, UNPROCESSED,
                OffsetDateTime.parse("2024-11-10T15:00:00+02:00").toInstant(),
                "transaction to process", Map.of("sender_amount_before", "9200", "receiver_amount_before", "10000", "sender_amount_after", "0", "receiver_amount_after", "19200"));
        Optional<Transaction> optionalExpectedTransaction = Optional.of(expectedTransaction);

        Transaction updatedTransaction =  transactionRepository.update(expectedTransaction);
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
    public void deleteById_should_delete_transaction_and_return_it_as_optional() {
        long transactionIdToDelete = 10L;
        Transaction transactionToDelete = new Transaction(10L, 8L, 1L, new BigDecimal("99999.00"), "EUR", DEBIT, UNPROCESSED, OffsetDateTime.parse("2022-12-06T19:00:10+01:00").toInstant(), "transaction 8", new HashMap<>());
        Optional<Transaction> optionalTransaction = Optional.of(transactionToDelete);


        Optional<Transaction> deletedTransaction = transactionRepository.deleteById(transactionIdToDelete);
        Optional<Transaction> deletedOptionalTransaction = transactionRepository.findById(10L);

        assertThat(deletedOptionalTransaction).isEmpty();
        assertThat(deletedTransaction)
                .usingRecursiveComparison()
                .isEqualTo(optionalTransaction);
    }
}