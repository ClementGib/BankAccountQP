package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.*;
import com.cdx.bas.domain.bank.transaction.type.TransactionProcessorServicePort;
import com.cdx.bas.domain.bank.transaction.validation.validator.TransactionValidator;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.COMPLETED;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static com.cdx.bas.domain.bank.transaction.type.TransactionType.CREDIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
class TransactionServiceTest {

    Clock clock = Mockito.mock(Clock.class);
    TransactionPersistencePort transactionRepository = Mockito.mock(TransactionPersistencePort.class);
    TransactionProcessorServicePort transactionProcessorServicePort = Mockito.mock(TransactionProcessorServicePort.class);
    TransactionValidator transactionValidator = Mockito.mock(TransactionValidator.class);

	TransactionServiceImpl transactionService = new TransactionServiceImpl(transactionRepository, transactionValidator, transactionProcessorServicePort);


    @Test
    void shouldReturnAllTransactions_whenRepositoryReturnsTransactions() {
        // Arrange
        Set<Transaction> transactions = Set.of(
                Transaction.builder().id(1L).build(),
                Transaction.builder().id(2L).build(),
                Transaction.builder().id(3L).build()
        );
        when(transactionRepository.getAll()).thenReturn(transactions);

        // Act
        Set<Transaction> actual = transactionService.getAll();

        // Assert
        assertThat(actual).isEqualTo(transactions);
        verify(transactionRepository).getAll();
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void shouldReturnTransactionCorrespondingToStatus_whenStatusIsValid() {
        // Arrange
        Set<Transaction> transactions = Set.of(
                Transaction.builder().id(1L).status(COMPLETED).build(),
                Transaction.builder().id(2L).status(COMPLETED).build(),
                Transaction.builder().id(3L).status(COMPLETED).build()
        );
        when(transactionRepository.findAllByStatus(COMPLETED)).thenReturn(transactions);

        // Act
        Set<Transaction> actual = transactionService.findAllByStatus("COMPLETED");

        // Assert
        assertThat(actual).isEqualTo(transactions);
        verify(transactionRepository).findAllByStatus(COMPLETED);
        verifyNoMoreInteractions(transactionRepository);
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
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldCreateTransaction_whenNewTransactionIsValid() {
        // Arrange
        Instant timestamp = Instant.parse("2024-03-14T12:00:00Z");
        NewDigitalTransaction newTransaction = new NewDigitalTransaction(99L, 77L,
                new BigDecimal("100"), "EUR",
                CREDIT, "transaction test", new HashMap<>());
        Transaction transactionToCreate = Transaction.builder()
                .id(null)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("100"))
                .currency("EUR")
                .label("transaction test")
                .type(CREDIT)
                .status(UNPROCESSED)
                .date(timestamp)
                .metadata(new HashMap<>())
                .build();
        when(clock.instant()).thenReturn(timestamp);

        // Act
        transactionService.createDigitalTransaction(newTransaction);

        // Assert
        verify(transactionValidator).validateNewDigitalTransaction(any());
        verify(transactionRepository).create(any());
        verifyNoMoreInteractions(transactionValidator, transactionRepository);
    }

    @Test
    void shouldThrowException_whenNewTransactionIsInvalid() {
        // Arrange
        Instant timestamp = Instant.parse("2024-03-14T12:00:00Z");
        Transaction invalidTransaction = new Transaction();
        invalidTransaction.setDate(timestamp);
        invalidTransaction.setStatus(UNPROCESSED);
        invalidTransaction.setMetadata(null);

        when(clock.instant()).thenReturn(timestamp);
        doThrow(new TransactionException("invalid transaction...")).when(transactionValidator).validateNewDigitalTransaction(any());

        try {
            // Act
            transactionService.createDigitalTransaction(new NewDigitalTransaction(null, null, null, null, null, null, null));
           fail("should throw exception");
        } catch (TransactionException exception) {
            // Assert
            assertThat(exception.getMessage()).isEqualTo("invalid transaction...");
            verify(transactionValidator).validateNewDigitalTransaction(any());
            verifyNoMoreInteractions(transactionValidator);
            verifyNoInteractions(transactionRepository);
        }
    }

    @Test
    void shouldFindTransaction_whenTransactionExists() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(1L)
                .amount(new BigDecimal(100))
                .emitterAccountId(100L)
                .receiverAccountId(200L)
                .type(CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now())
                .label("deposit of 100 euros")
                .build();
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // Act
        Transaction actualTransaction = transactionService.findTransaction(1L);

        // Assert
        assertThat(actualTransaction).isEqualTo(transaction);
        verify(transactionRepository).findById(1L);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void shouldReturnNull_whenTransactionDoesNotExist() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Transaction actualTransaction = transactionService.findTransaction(1L);

        // Assert
        assertThat(actualTransaction).isNull();
        verify(transactionRepository).findById(1L);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void shouldProcessBankAccountCredit_whenTransactionHasCreditType() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(1L)
                .amount(new BigDecimal(100))
                .emitterAccountId(100L)
                .receiverAccountId(200L)
                .type(CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now())
                .label("deposit of 100 euros")
                .build();

        // Act
        transactionService.processDigitalTransaction(transaction);

        // Assert
        verify(transactionProcessorServicePort).credit(transaction);
        verifyNoMoreInteractions(transactionProcessorServicePort);
    }

    @Test
    void shouldProcessBankAccountDeposit_whenValidTransactionDeposit() {
        // Arrange
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bill", "50,25,25");
        NewCashTransaction newTransaction = new NewCashTransaction(100L, new BigDecimal("100"), "EUR", metadata);
        Instant timestamp = Instant.parse("2024-03-14T12:00:00Z");
        when(clock.instant()).thenReturn(timestamp);

        // Act
        transactionService.deposit(newTransaction);

        // Assert
        verify(transactionProcessorServicePort).deposit(any());
        verifyNoMoreInteractions(transactionProcessorServicePort);
    }

    @Test
    void shouldThrowValidationException_whenInvalidTransactionDeposit() {
        // Arrange
        NewCashTransaction newTransaction = new NewCashTransaction(null, null, null, null);
        doThrow(new TransactionException("""
                Amount must not be null.
                Metadata must not be null.
                Emitter account id must not be null.
                Currency must not be null.
                """))
                .when(transactionValidator).validateCashTransaction(any());

        // Act
        try {
            transactionService.deposit(newTransaction);
        } catch (TransactionException exception) {
            assertThat(exception.getMessage()).isEqualTo("""
                Amount must not be null.
                Metadata must not be null.
                Emitter account id must not be null.
                Currency must not be null.
                """);
        }
    }

    @Test
    void shouldProcessBankAccountWithdraw_whenValidTransactionWithdraw() {
        // Arrange
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bill", "50,25,25");
        NewCashTransaction newTransaction = new NewCashTransaction(100L, new BigDecimal("100"), "EUR", metadata);
        Instant timestamp = Instant.parse("2024-03-14T12:00:00Z");
        when(clock.instant()).thenReturn(timestamp);

        // Act
        transactionService.withdraw(newTransaction);

        // Assert
        verify(transactionProcessorServicePort).withdraw(any());
        verifyNoMoreInteractions(transactionProcessorServicePort);
    }
}
