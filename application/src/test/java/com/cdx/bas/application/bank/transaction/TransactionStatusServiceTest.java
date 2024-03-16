package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class TransactionStatusServiceTest {

    @Inject
    TransactionStatusServicePort transactionStatusServicePort;

    @InjectMock
    TransactionRepository transactionPersistencePort;

    @Test
    @Transactional
    public void setAsOutstanding_shouldSetStatusAndUpdate_whenStatusIsOutstanding() {
        Transaction transaction = Transaction.builder()
                .status(UNPROCESSED)
                .build();

        Transaction expectedTransaction = Transaction.builder()
                .status(OUTSTANDING)
                .build();

        when(transactionPersistencePort.update(expectedTransaction)).thenReturn(expectedTransaction);

        Transaction actualTransaction = transactionStatusServicePort.setAsOutstanding(transaction);
        assertThat(actualTransaction).isEqualTo(expectedTransaction);
        verify(transactionPersistencePort).update(actualTransaction);
    }

    @Test
    @Transactional
    public void setAsOutstanding_shouldThrowTransactionException_whenStatusIsNotOutstanding() {
        Transaction transaction = Transaction.builder()
                .status(ERROR)
                .build();

        try {
            transactionStatusServicePort.setAsOutstanding(transaction);
        } catch (TransactionException transactionException) {
            assertThat(transactionException.getMessage()).isEqualTo("Transaction is not longer unprocessed.");
        }
    }

    @Test
    @Transactional
    public void setStatus_shouldSetStatusAndUpdate_whenTransactionIsValid() {
        Transaction transaction = Transaction.builder()
                .status(UNPROCESSED)
                .build();

        Map<String, String> metadata = Map.of("error", "Transaction 1 deposit error for amount 100: error");
        Transaction expectedTransaction = Transaction.builder()
                .status(ERROR)
                .metadata(metadata)
                .build();

        when(transactionPersistencePort.update(expectedTransaction)).thenReturn(expectedTransaction);

        Transaction actualTransaction = transactionStatusServicePort.saveStatus(transaction, ERROR, metadata);
        assertThat(actualTransaction)
                .extracting(Transaction::getStatus, Transaction::getMetadata)
                .containsExactly(ERROR, metadata);
        verify(transactionPersistencePort).update(actualTransaction);
    }

    @Test
    @Transactional
    public void setStatus_shouldThrowTransactionException_whenTransactionIsNull() {
        try {
            transactionStatusServicePort.saveStatus(null, COMPLETED, new HashMap<>());
        } catch (TransactionException transactionException) {
            assertThat(transactionException.getMessage()).isEqualTo("Transaction is null.");
        }
    }
}