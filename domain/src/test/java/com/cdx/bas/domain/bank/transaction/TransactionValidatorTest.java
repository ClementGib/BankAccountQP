package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
class TransactionValidatorTest {

    @Inject
    TransactionValidator transactionValidator;

    @Test
    void shouldDoNothing_whenNewDigitalTransactionIsValid() {
        // Arrange
        Transaction creditTransaction = new Transaction();
        creditTransaction.setId(null);
        creditTransaction.setEmitterAccountId(1L);
        creditTransaction.setReceiverAccountId(2L);
        creditTransaction.setAmount(new BigDecimal("1"));
        creditTransaction.setCurrency("EUR");
        creditTransaction.setType(TransactionType.CREDIT);
        creditTransaction.setStatus(TransactionStatus.UNPROCESSED);
        creditTransaction.setDate(Instant.now());
        creditTransaction.setLabel("new transaction");
        creditTransaction.setMetadata(new HashMap<>());

        // Act
        transactionValidator.validateNewDigitalTransaction(creditTransaction);
    }

    @Test
    void shouldThrowTransactionExceptionWithMissingViolation_whenNewDigitalTransactionIsEmpty() {
        // Arrange
        Transaction creditTransaction = new Transaction();
        try {
            // Act
            transactionValidator.validateNewDigitalTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
                    "Amount must not be null.",
                    "Emitter account id must not be null.",
                    "Currency must not be null.",
                    "Label must not be null.",
                    "Type must not be null.",
                    "Receiver account id must not be null.",
                    "Status must not be null.");
            List<String> actualErrors = Arrays.stream(transactionException.getMessage().split("\\r?\\n")).toList();
            assertThat(actualErrors)
                    .hasSize(8)
                    .containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }

    @Test
    void shouldThrowTransactionExceptionWithWrongValue_whenNewDigitalTransactionHasWrongValue() {
        // Arrange
        Transaction creditTransaction = new Transaction();
        creditTransaction.setId(1L);
        creditTransaction.setEmitterAccountId(-1L);
        creditTransaction.setReceiverAccountId(-1L);
        creditTransaction.setAmount(new BigDecimal("0"));
        creditTransaction.setCurrency("NFC");
        creditTransaction.setType(TransactionType.DEPOSIT);
        creditTransaction.setStatus(TransactionStatus.ERROR);

        try {
            // Act
            transactionValidator.validateNewDigitalTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
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
                    .hasSize(9)
                    .containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }

    @Test
    void shouldDoNothing_whenExistingDigitalTransactionIsValid() {
        // Arrange
        Transaction creditTransaction = new Transaction();
        creditTransaction.setId(100L);
        creditTransaction.setEmitterAccountId(1L);
        creditTransaction.setReceiverAccountId(2L);
        creditTransaction.setAmount(new BigDecimal("1"));
        creditTransaction.setCurrency("EUR");
        creditTransaction.setType(TransactionType.CREDIT);
        creditTransaction.setStatus(TransactionStatus.UNPROCESSED);
        creditTransaction.setDate(Instant.now());
        creditTransaction.setLabel("new transaction");
        creditTransaction.setMetadata(new HashMap<>());

        // Act
        transactionValidator.validateExistingDigitalTransaction(creditTransaction);
    }

    @Test
    void shouldThrowTransactionException_whenExistingDigitalTransactionIsEmpty() {
        // Arrange
        Transaction creditTransaction = new Transaction();
        try {
            // Act
            transactionValidator.validateExistingDigitalTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
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
                    .hasSize(9)
                    .containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }
    @Test
    void shouldThrowTransactionExceptionWithWrongValue_whenExistingDigitalTransactionHasWrongValue() {
        // Arrange
        Transaction creditTransaction = new Transaction();
        creditTransaction.setId(-1L);
        creditTransaction.setEmitterAccountId(-1L);
        creditTransaction.setReceiverAccountId(-1L);
        creditTransaction.setAmount(new BigDecimal("0"));
        creditTransaction.setCurrency("NFC");
        creditTransaction.setType(TransactionType.DEPOSIT);

        try {
            // Act
            transactionValidator.validateExistingDigitalTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
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
                    .hasSize(9)
                    .containsExactlyInAnyOrderElementsOf(expectedErrors);
        }
    }

    @Test
    void shouldDoNothing_whenCashTransactionIsValid() {
        // Arrange
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bill", "5,5");
        Transaction creditTransaction = new Transaction();
        creditTransaction.setEmitterAccountId(1L);
        creditTransaction.setAmount(new BigDecimal("10"));
        creditTransaction.setCurrency("EUR");
        creditTransaction.setType(TransactionType.WITHDRAW);
        creditTransaction.setStatus(TransactionStatus.UNPROCESSED);
        creditTransaction.setDate(Instant.now());
        creditTransaction.setLabel("new transaction");
        creditTransaction.setMetadata(metadata);

        // Act
        transactionValidator.validateCashTransaction(creditTransaction);
    }

    @Test
    void shouldThrowTransactionException_whenNewCashTransactionIsEmpty() {
        // Arrange
        Transaction creditTransaction = new Transaction();
        try {
            // Act
            transactionValidator.validateCashTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
                    "Amount must not be null.",
                    "Bill must be define for cash movements.",
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
    void shouldThrowTransactionException_whenNewCashTransactionHasWrongValue() {
        // Arrange
        Transaction creditTransaction = new Transaction();
        creditTransaction.setId(-1L);
        creditTransaction.setEmitterAccountId(-1L);
        creditTransaction.setReceiverAccountId(-1L);
        creditTransaction.setAmount(new BigDecimal("0"));
        creditTransaction.setCurrency("NFC");
        creditTransaction.setType(TransactionType.DEBIT);
        creditTransaction.setStatus(TransactionStatus.ERROR);

        try {
            // Act
            transactionValidator.validateCashTransaction(creditTransaction);
            fail();
        } catch (TransactionException transactionException) {
            // Assert
            List<String> expectedErrors = List.of("Date must not be null.",
                    "Bill must be define for cash movements.",
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