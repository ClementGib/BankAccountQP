package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.bank.transaction.validation.validator.TransactionValidator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.ERROR;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static com.cdx.bas.domain.bank.transaction.type.TransactionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
class TransactionValidatorTest {

    @Inject
    TransactionValidator transactionValidator;

    @Test
    public void shouldDoNothing_whenNewDigitalTransactionIsValid() {
        // Arrange
        Transaction creditTransaction = Transaction.builder()
                .id(null)
                .emitterAccountId(1L)
                .receiverAccountId(2L)
                .amount(new BigDecimal("1"))
                .currency("EUR")
                .type(CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now())
                .label("new transaction")
                .metadata(new HashMap<>())
                .build();

        // Act
        transactionValidator.validateNewDigitalTransaction(creditTransaction);
    }

    @Test
    public void shouldThrowTransactionExceptionWithMissingViolation_whenNewDigitalTransactionIsEmpty() {
        // Arrange
        Transaction creditTransaction = Transaction.builder()
                .build();
        try {
            // Act
            transactionValidator.validateNewDigitalTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
                    "Metadata must not be null.",
                    "Amount must not be null.",
                    "Emitter account id must not be null.",
                    "Currency must not be null.",
                    "Label must not be null.",
                    "Type must not be null.",
                    "Receiver account id must not be null.",
                    "Status must not be null.");
            List<String> actualErrors = Arrays.stream(transactionException.getMessage().split("\\r?\\n")).toList();
            assertThat(actualErrors)
                    .hasSize(9)
                    .containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }

    @Test
    public void shouldThrowTransactionExceptionWithWrongValue_whenNewDigitalTransactionHasWrongValue() {
        // Arrange
        Transaction creditTransaction = Transaction.builder()
                .id(1L)
                .emitterAccountId(-1L)
                .receiverAccountId(-1L)
                .amount(new BigDecimal("0"))
                .currency("NFC")
                .type(DEPOSIT)
                .status(ERROR)
                .build();
        try {
            // Act
            transactionValidator.validateNewDigitalTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
                    "Metadata must not be null.",
                    "Amount must be positive and greater than 0.",
                    "Emitter account id  must be positive.",
                    "Currency should be in the exchange rate map.",
                    "Label must not be null.",
                    "Id must be null for new transaction.",
                    "Unexpected transaction types DEPOSIT, expected type: CREDIT, DEBIT.",
                    "Receiver account id  must be positive.",
                    "Unexpected transaction status ERROR, expected status: UNPROCESSED.");
            List<String> actualErrors = Arrays.stream(transactionException.getMessage().split("\\r?\\n")).toList();
            assertThat(actualErrors)
                    .hasSize(10)
                    .containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }

    @Test
    public void shouldDoNothing_whenExistingDigitalTransactionIsValid() {
        // Arrange
        Transaction creditTransaction = Transaction.builder()
                .id(100L)
                .emitterAccountId(1L)
                .receiverAccountId(2L)
                .amount(new BigDecimal("1"))
                .currency("EUR")
                .type(CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now())
                .label("new transaction")
                .metadata(new HashMap<>())
                .build();
        // Act
        transactionValidator.validateExistingDigitalTransaction(creditTransaction);
    }

    @Test
    public void shouldThrowTransactionException_whenExistingDigitalTransactionIsEmpty() {
        // Arrange
        Transaction creditTransaction = Transaction.builder()
                .build();
        try {
            // Act
            transactionValidator.validateExistingDigitalTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
                    "Metadata must not be null.",
                    "Amount must not be null.",
                    "Emitter account id must not be null.",
                    "Currency must not be null.",
                    "Label must not be null.",
                    "Id must not be null for existing transaction.",
                    "Type must not be null.",
                    "Status must not be null.",
                    "Receiver account id must not be null.");
            List<String> actualErrors = Arrays.stream(transactionException.getMessage().split("\\r?\\n")).toList();
            assertThat(actualErrors)
                    .hasSize(10)
                    .containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }
    @Test
    public void shouldThrowTransactionExceptionWithWrongValue_whenExistingDigitalTransactionHasWrongValue() {
        // Arrange
        Transaction creditTransaction = Transaction.builder()
                .id(-1L)
                .emitterAccountId(-1L)
                .receiverAccountId(-1L)
                .amount(new BigDecimal("0"))
                .currency("NFC")
                .type(DEPOSIT)
                .build();
        try {
            // Act
            transactionValidator.validateExistingDigitalTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
                    "Metadata must not be null.",
                    "Amount must be positive and greater than 0.",
                    "Emitter account id  must be positive.",
                    "Currency should be in the exchange rate map.",
                    "Label must not be null.",
                    "Id must be positive.",
                    "Unexpected transaction types DEPOSIT, expected type: CREDIT, DEBIT.",
                    "Status must not be null.",
                    "Receiver account id  must be positive.");
            List<String> actualErrors = Arrays.stream(transactionException.getMessage().split("\\r?\\n")).toList();
            assertThat(actualErrors)
                    .hasSize(10)
                    .containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }

    @Test
    public void shouldDoNothing_whenCashTransactionIsValid() {
        // Arrange
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bill", "5,5");
        Transaction creditTransaction = Transaction.builder()
                .emitterAccountId(1L)
                .amount(new BigDecimal("10"))
                .currency("EUR")
                .type(WITHDRAW)
                .status(UNPROCESSED)
                .date(Instant.now())
                .label("new transaction")
                .metadata(metadata)
                .build();
        // Act
        transactionValidator.validateCashTransaction(creditTransaction);
    }

    @Test
    public void shouldThrowTransactionException_whenNewCashTransactionIsEmpty() {
        // Arrange
        Transaction creditTransaction = Transaction.builder()
                .build();
        try {
            // Act
            transactionValidator.validateCashTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
                    "Metadata must not be null.",
                    "Amount must not be null.",
                    "Emitter account id must not be null.",
                    "Currency must not be null.",
                    "Label must not be null.",
                    "Type must not be null.",
                    "Status must not be null.");
            List<String> actualErrors = Arrays.stream(transactionException.getMessage().split("\\r?\\n")).toList();
            assertThat(actualErrors)
                    .hasSize(8)
                    .containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }

    @Test
    public void shouldThrowTransactionException_whenNewCashTransactionHasWrongValue() {
        // Arrange
        Transaction creditTransaction = Transaction.builder()
                .id(-1L)
                .emitterAccountId(-1L)
                .receiverAccountId(-1L)
                .amount(new BigDecimal("0"))
                .currency("NFC")
                .type(DEBIT)
                .status(ERROR)
                .build();
        try {
            // Act
            transactionValidator.validateCashTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
                    "Metadata must not be null.",
                    "Amount must be greater than 10 for cash movement.",
                    "Emitter account id  must be positive.",
                    "Currency should be in the exchange rate map.",
                    "Label must not be null.",
                    "Id must be positive.",
                    "Unexpected transaction types DEBIT, expected type: DEPOSIT, WITHDRAW.",
                    "Receiver account id  must be positive.",
                    "Unexpected transaction status ERROR, expected status: UNPROCESSED.");
            List<String> actualErrors = Arrays.stream(transactionException.getMessage().split("\\r?\\n")).toList();
            assertThat(actualErrors)
                    .hasSize(10)
                    .containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }

}