package com.cdx.bas.client.bank.transaction;

import com.cdx.bas.application.bank.transaction.TransactionRepository;
import com.cdx.bas.domain.bank.transaction.category.NewCashTransaction;
import com.cdx.bas.domain.bank.transaction.Transaction;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.*;
import static com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType.*;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithTestResource(H2DatabaseTestResource.class)
class TransactionResourceTest {

    @Inject
    TransactionRepository transactionRepository;

    @Inject
    TransactionResource transactionResource;

    @Test
    @Order(1)
    void getAll_shouldReturnAllTransactions() {
        Set<Transaction> expectedTransactions = Set.of(
                new Transaction(1L, 1L, 2L, new BigDecimal("1600.00"), "EUR", CREDIT, COMPLETED, Instant.parse("2024-06-06T11:00:00+00:00"), "transaction 1", Map.of("emitter_amount_before", "2000", "receiver_amount_before", "0", "emitter_amount_after", "400", "receiver_amount_after", "1600")),
                new Transaction(2L, 6L, 3L, new BigDecimal("9200.00"), "EUR", CREDIT, ERROR, Instant.parse("2024-07-10T14:00:00+00:00"), "transaction 2", Map.of("error", "Transaction 2 deposit error for amount 9200 ...")),
                new Transaction(3L, 6L, 3L, new BigDecimal("9200.00"), "EUR", CREDIT, COMPLETED, Instant.parse("2024-07-10T14:00:00+00:00"), "transaction 3", Map.of("emitter_amount_before", "9200", "receiver_amount_before", "10000", "emitter_amount_after", "0", "receiver_amount_after", "19200")),
                new Transaction(4L, 5L, 1L, new BigDecimal("100000.00"), "EUR", CREDIT, REFUSED, Instant.parse("2024-07-10T14:00:00+00:00"), "transaction 4", Map.of("error", "Transaction 4 deposit error for amount 100000 ...")),
                new Transaction(5L, 2L, 1L, new BigDecimal("600.99"), "EUR", CREDIT, UNPROCESSED, Instant.parse("2024-11-06T17:00:00+00:00"), "transaction 5", new HashMap<>()),
                new Transaction(6L, 1L, 7L, new BigDecimal("2000.00"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-11-06T17:30:00+00:00"), "transaction 6", new HashMap<>()),
                new Transaction(7L, 3L, 1L, new BigDecimal("1000.00"), "EUR", CREDIT, UNPROCESSED, Instant.parse("2024-12-06T17:00:00+00:00"), "transaction 7", new HashMap<>()),
                new Transaction(8L, 4L, 2L, new BigDecimal("300.80"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-12-06T18:00:00+00:00"), "transaction 8", new HashMap<>()),
                new Transaction(9L, 8L, 7L, new BigDecimal("5000.00"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 9", new HashMap<>()),
                new Transaction(10L, 1L, null, new BigDecimal("100.00"), "EUR", DEPOSIT, COMPLETED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 10", new HashMap<>()),
                new Transaction(11L, 1L, null, new BigDecimal("200.00"), "EUR", WITHDRAW, COMPLETED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 11", new HashMap<>())
        );

        Set<Transaction> actualTransactions = transactionResource.getAll();
        assertThat(actualTransactions).containsExactlyInAnyOrderElementsOf(expectedTransactions);
    }

    @Test
    @Order(2)
    void getAllByStatus_shouldReturnEmptySet_whenStatusIsInvalid() {
        Set<Transaction> expectedCustomers = Collections.emptySet();

        Set<Transaction> actualTransactions = transactionResource.getAllByStatus("");
        assertThat(actualTransactions)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedCustomers);
    }

    @Test
    @Order(3)
    void getAllByStatus_shouldReturnTransactionWithCompletedStatus() {
        Set<Transaction> expectedCustomers = Set.of(
                new Transaction(1L, 1L, 2L, new BigDecimal("1600.00"), "EUR", CREDIT, COMPLETED, Instant.parse("2024-06-06T11:00:00+00:00"), "transaction 1", Map.of("emitter_amount_before", "2000", "receiver_amount_before", "0", "emitter_amount_after", "400", "receiver_amount_after", "1600")),
                new Transaction(3L, 6L, 3L, new BigDecimal("9200.00"), "EUR", CREDIT, COMPLETED, Instant.parse("2024-07-10T14:00:00+00:00"), "transaction 3", Map.of("emitter_amount_before", "9200", "receiver_amount_before", "10000", "emitter_amount_after", "0", "receiver_amount_after", "19200")),
                new Transaction(10L, 1L, null, new BigDecimal("100.00"), "EUR", DEPOSIT, COMPLETED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 10", new HashMap<>()),
                new Transaction(11L, 1L, null, new BigDecimal("200.00"), "EUR", WITHDRAW, COMPLETED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 11", new HashMap<>())
        );

        Set<Transaction> actualTransactions = transactionResource.getAllByStatus("completed");
        assertThat(actualTransactions)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedCustomers);
    }

    @Test
    @Order(4)
    void getAllByStatus_shouldReturnTransactionWithUnprocessedStatus() {
        Set<Transaction> expectedCustomers = Set.of(
                new Transaction(5L, 2L, 1L, new BigDecimal("600.99"), "EUR", CREDIT, UNPROCESSED, Instant.parse("2024-11-06T17:00:00+00:00"), "transaction 5", new HashMap<>()),
                new Transaction(6L, 1L, 7L, new BigDecimal("2000.00"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-11-06T17:30:00+00:00"), "transaction 6", new HashMap<>()),
                new Transaction(7L, 3L, 1L, new BigDecimal("1000.00"), "EUR", CREDIT, UNPROCESSED, Instant.parse("2024-12-06T17:00:00+00:00"), "transaction 7", new HashMap<>()),
                new Transaction(8L, 4L, 2L, new BigDecimal("300.80"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-12-06T18:00:00+00:00"), "transaction 8", new HashMap<>()),
                new Transaction(9L, 8L, 7L, new BigDecimal("5000.00"), "EUR", DEBIT, UNPROCESSED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 9", new HashMap<>())
        );

        Set<Transaction> actualTransactions = transactionResource.getAllByStatus("unprocessed");
        assertThat(actualTransactions)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedCustomers);
    }

    @Test
    @Order(5)
    void getAllByStatus_shouldReturnTransactionWithErrorStatus() {
        Set<Transaction> expectedCustomers = Set.of(
                new Transaction(2L, 6L, 3L, new BigDecimal("9200.00"), "EUR", CREDIT, ERROR, Instant.parse("2024-07-10T14:00:00+00:00"), "transaction 2", Map.of("error", "Transaction 2 deposit error for amount 9200 ..."))
        );

        Set<Transaction> actualTransactions = transactionResource.getAllByStatus("error");
        assertThat(actualTransactions)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedCustomers);
    }

    @Test
    @Order(6)
    void getAllByStatus_shouldReturnTransactionWithRefusedStatus() {
        Set<Transaction> expectedCustomers = Set.of(
                new Transaction(4L, 5L, 1L, new BigDecimal("100000.00"), "EUR", CREDIT, REFUSED, Instant.parse("2024-07-10T14:00:00+00:00"), "transaction 4", Map.of("error", "Transaction 4 deposit error for amount 100000 ..."))
        );

        Set<Transaction> actualTransactions = transactionResource.getAllByStatus("refused");
        assertThat(actualTransactions)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedCustomers);
    }

    @Order(7)
    @Test
    void findById_shouldReturnTransaction_whenTransactionFound() {
        Transaction expectedTransaction = new Transaction(1L, 1L, 2L, new BigDecimal("1600.00"), "EUR", CREDIT, COMPLETED, Instant.parse("2024-06-06T11:00:00+00:00"), "transaction 1", Map.of("emitter_amount_before", "2000", "receiver_amount_before", "0", "emitter_amount_after", "400", "receiver_amount_after", "1600"));

        Transaction actualTransaction = transactionResource.findById(1L);
        assertThat(actualTransaction).isEqualTo(expectedTransaction);

    }

    @Order(8)
    @Test
    void findById_shouldReturnEmptyTransaction_whenTransactionNotFound() {
        long id = 99L;
        try {
            transactionResource.findById(id);
        } catch (WebApplicationException exception) {
            assertThat(exception).hasMessage("Transaction: searching failed - not found\n" +
                    "Transaction id:" + id);
        }

    }

    @Test
    @Order(9)
    void withdraw_shouldReturnAcceptedResponse_whenTransactionIsValidated() {
        Instant timestampBefore = Instant.now();
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bill", "500,500");
        NewCashTransaction validNewTransaction = new NewCashTransaction(2L, new BigDecimal("1000.00"), "EUR", metadata);
        Transaction expectedCreatedTransaction = new Transaction(20L, 2L, null, new BigDecimal("1000.00"), "EUR", WITHDRAW, COMPLETED, timestampBefore, "withdraw:1000.00 EUR",
                Map.of("bill", "500,500", "emitter_amount_after", "600.00", "emitter_amount_before", "1600.00"));

        Response actualResponse = transactionResource.withdraw(validNewTransaction);
        Transaction actualTransaction = transactionResource.findById(20L);
        Instant timestampAfter = Instant.now();
        String expectedMessage = "Transaction: withdraw accepted";
        assertThat(actualResponse.getEntity()).isEqualTo(expectedMessage);
        assertThat(actualTransaction)
                .usingRecursiveComparison()
                .ignoringFields("date")
                .isEqualTo(expectedCreatedTransaction);
        assertThat(actualTransaction.getDate()).isAfter(timestampBefore).isBefore(timestampAfter);
    }

    @Test
    @Order(10)
    void deposit_shouldReturnAcceptedResponse_whenTransactionIsValidated() {
        Instant timestampBefore = Instant.now();
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bill", "500,500");
        NewCashTransaction validNewTransaction = new NewCashTransaction(1L, new BigDecimal("1000.00"), "EUR", metadata);
        Transaction expectedCreatedTransaction = new Transaction(21L, 1L, null, new BigDecimal("1000.00"), "EUR", DEPOSIT, COMPLETED, timestampBefore, "deposit:1000.00 EUR",
                Map.of("bill", "500,500", "emitter_amount_before", "400.00", "emitter_amount_after", "1400.00"));

        Response actualResponse = transactionResource.deposit(validNewTransaction);
        Transaction actualTransaction = transactionResource.findById(21L);
        Instant timestampAfter = Instant.now();
        String expectedMessage = "Transaction: deposit accepted";
        assertThat(actualResponse.getEntity()).isEqualTo(expectedMessage);
        assertThat(actualTransaction)
                .usingRecursiveComparison()
                .ignoringFields("date")
                .isEqualTo(expectedCreatedTransaction);
        assertThat(actualTransaction.getDate()).isAfter(timestampBefore).isBefore(timestampAfter);
    }


    @Test
    @Order(11)
    void withdraw_shouldReturnErrorResponse_whenTransactionIsInvalid() {
        NewCashTransaction invalidNewTransaction = new NewCashTransaction(1L, null, null, null);
        List<String> expectedLines = Arrays.asList("Withdraw transaction: withdraw refused - domain error",
                "Transaction id:null",
                "Error:Amount must not be null.",
                "Bill must be define for cash movements.",
                "Currency must not be null.");

        Response actualResponse = transactionResource.withdraw(invalidNewTransaction);
        List<String> actualLines = Arrays.asList(actualResponse.getEntity().toString().split("\n"));
        assertThat(actualLines)
                .containsExactlyInAnyOrderElementsOf(expectedLines);
    }

    @Test
    @Order(12)
    void deposit_shouldReturnErrorResponse_whenTransactionIsInvalid() {
        NewCashTransaction invalidNewTransaction = new NewCashTransaction(null, null, null, null);
        List<String> expectedLines = Arrays.asList(
                "Deposit transaction: deposit refused - domain error",
                "Transaction id:null",
                "Error:Amount must not be null.",
                "Bill must be define for cash movements.",
                "Emitter account id must not be null.",
                "Currency must not be null.");

        Response actualResponse = transactionResource.deposit(invalidNewTransaction);
        List<String> actualLines = Arrays.asList(actualResponse.getEntity().toString().split("\n"));
        assertThat(actualLines)
                .containsExactlyInAnyOrderElementsOf(expectedLines);
    }
}