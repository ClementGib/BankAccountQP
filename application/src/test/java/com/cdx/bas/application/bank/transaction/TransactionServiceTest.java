package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.TransactionPersistencePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.category.NewCashTransaction;
import com.cdx.bas.domain.bank.transaction.category.NewDigitalTransaction;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType.*;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionServiceTest {

    @Inject
    TransactionPersistencePort transactionRepository;

    @Inject
    TransactionServicePort transactionService;

    @Test
    @Order(1)
    void shouldReturnAllTransactions_whenRepositoryReturnsTransactions() {
        // Arrange
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

        // Act
        Set<Transaction> actualTransactions = transactionService.getAll();

        // Assert
        assertThat(actualTransactions).isEqualTo(expectedTransactions);
    }

    @Test
    @Order(2)
    void shouldReturnTransactionCorrespondingToStatus_whenStatusIsValid() {
        // Arrange
        Set<Transaction> expectedTransactions = Set.of(
                new Transaction(1L, 1L, 2L, new BigDecimal("1600.00"), "EUR", CREDIT, COMPLETED, Instant.parse("2024-06-06T11:00:00+00:00"), "transaction 1", Map.of("emitter_amount_before", "2000", "receiver_amount_before", "0", "emitter_amount_after", "400", "receiver_amount_after", "1600")),
                new Transaction(3L, 6L, 3L, new BigDecimal("9200.00"), "EUR", CREDIT, COMPLETED, Instant.parse("2024-07-10T14:00:00+00:00"), "transaction 3", Map.of("emitter_amount_before", "9200", "receiver_amount_before", "10000", "emitter_amount_after", "0", "receiver_amount_after", "19200")),
                new Transaction(10L, 1L, null, new BigDecimal("100.00"), "EUR", DEPOSIT, COMPLETED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 10", new HashMap<>()),
                new Transaction(11L, 1L, null, new BigDecimal("200.00"), "EUR", WITHDRAW, COMPLETED, Instant.parse("2024-12-06T18:00:10+00:00"), "transaction 11", new HashMap<>())
        );

        // Act
        Set<Transaction> actualTransactions = transactionService.findAllByStatus("COMPLETED");

        // Assert
        assertThat(actualTransactions).isEqualTo(expectedTransactions);
    }

    @Test
    void shouldThrowException_whenStatusIsInvalid() {
        // Act
        try {
            transactionService.findAllByStatus("INVALID");
        } catch (IllegalArgumentException exception) {
            // Assert
            assertThat(exception.getMessage()).isEqualTo("Invalid status: INVALID");
        }
    }

    @Test
    void shouldCreateTransaction_whenNewTransactionIsValid() {
        // Arrange
        long id = 20L;
        NewDigitalTransaction newTransaction = new NewDigitalTransaction(1L, 2L,
                new BigDecimal("100"), "EUR",
                CREDIT, "transaction test", new HashMap<>());

        // Act
        transactionService.createDigitalTransaction(newTransaction);

        // Assert
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        assertThat(optionalTransaction).isPresent();
        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setId(id);
        expectedTransaction.setEmitterAccountId(1L);
        expectedTransaction.setReceiverAccountId(2L);
        expectedTransaction.setAmount(new BigDecimal("100.00"));
        expectedTransaction.setCurrency("EUR");
        expectedTransaction.setLabel("transaction test");
        expectedTransaction.setType(TransactionType.CREDIT);
        expectedTransaction.setStatus(TransactionStatus.UNPROCESSED);
        expectedTransaction.setMetadata(new HashMap<>());
        assertThat(optionalTransaction.get())
                .usingRecursiveComparison()
                .ignoringFields("date")
                .isEqualTo(expectedTransaction);
    }

    @Test
    void shouldThrowException_whenNewTransactionIsInvalid() {
        // Arrange
        Instant timestamp = Instant.parse("2024-03-14T12:00:10+00:00");
        Transaction invalidTransaction = new Transaction();
        invalidTransaction.setDate(timestamp);
        invalidTransaction.setStatus(UNPROCESSED);
        invalidTransaction.setMetadata(null);

        try {
            // Act
            transactionService.createDigitalTransaction(new NewDigitalTransaction(null, null, null, null, null, null, null));
            fail("should throw exception");
        } catch (TransactionException exception) {
            // Assert
            List<String> expectedErrors = List.of("Amount must not be null.",
                    "Metadata must not be null.",
                    "Emitter account id must not be null.",
                    "Currency must not be null.",
                    "Label must not be null.",
                    "Type must not be null.",
                    "Receiver account id must not be null.");
            List<String> actualErrorLines = List.of(exception.getMessage().split("\n"));
            assertThat(actualErrorLines).containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }

    @Test
    void shouldFindTransaction_whenTransactionExists() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(new BigDecimal("1600.00"));
        transaction.setDate(Instant.parse("2024-06-06T11:00:00+00:00"));
        transaction.setEmitterAccountId(1L);
        transaction.setReceiverAccountId(2L);
        transaction.setCurrency("EUR");
        transaction.setType(TransactionType.CREDIT);
        transaction.setStatus(COMPLETED);
        transaction.setLabel("transaction 1");
        transaction.setMetadata(Map.of("emitter_amount_before", "2000", "receiver_amount_before", "0", "emitter_amount_after", "400", "receiver_amount_after", "1600"));

        // Act
        Transaction actualTransaction = transactionService.findTransaction(1L);

        // Assert
        assertThat(actualTransaction)
                .usingRecursiveComparison()
                .isEqualTo(transaction);
    }

    @Test
    void shouldReturnNull_whenTransactionDoesNotExist() {
        try {
            // Act
            transactionService.findTransaction(99L);
        } catch (TransactionException transactionException) {
            // Assert
            assertThat(transactionException.getMessage()).isEqualTo("Transaction: searching failed - not found\n" +
                    "Transaction id:99");
        }
    }

    @Test
    @Disabled
    void shouldProcessBankAccountCredit_whenTransactionHasCreditType() {
        // Arrange
        long id = 1L;
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setEmitterAccountId(id);
        transaction.setReceiverAccountId(2L);
        transaction.setType(TransactionType.CREDIT);
        transaction.setStatus(TransactionStatus.UNPROCESSED);
        transaction.setCurrency("EUR");
        transaction.setDate(Instant.now());
        transaction.setLabel("deposit of 100 euros");

        // Act
        transactionService.processDigitalTransaction(transaction);

        // Assert
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        assertThat(optionalTransaction)
                .contains(transaction);
    }

    @Test
    void shouldProcessBankAccountDeposit_whenValidTransactionDeposit() {
        // Arrange
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bill", "50,25,25");
        NewCashTransaction newTransaction = new NewCashTransaction(1L, new BigDecimal("100"), "EUR", metadata);

        // Act
        transactionService.deposit(newTransaction);

        // Assert
        assertThat(transactionRepository.findById(20L)).isPresent();
    }

    @Test
    void shouldThrowValidationException_whenInvalidTransactionDeposit() {
        // Arrange
        NewCashTransaction newTransaction = new NewCashTransaction(null, null, null, null);

        // Act
        try {
            transactionService.deposit(newTransaction);
        } catch (TransactionException exception) {
            List<String> expectedErrors = List.of("Deposit transaction: deposit refused - domain error",
                    "Transaction id:null",
                    "Error:Amount must not be null.",
                    "Bill must be define for cash movements.",
                    "Emitter account id must not be null.",
                    "Currency must not be null.");
            List<String> actualErrorLines = List.of(exception.getMessage().split("\n"));
            assertThat(actualErrorLines).containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }

    @Test
    void shouldProcessBankAccountWithdraw_whenValidTransactionWithdraw() {
        // Arrange
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bill", "50,25,25");
        NewCashTransaction newTransaction = new NewCashTransaction(1L, new BigDecimal("100"), "EUR", metadata);

        // Act
        transactionService.withdraw(newTransaction);

        // Assert
        assertThat(transactionRepository.findById(20L)).isPresent();
    }
}
