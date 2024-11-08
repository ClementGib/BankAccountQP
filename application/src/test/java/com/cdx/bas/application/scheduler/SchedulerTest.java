package com.cdx.bas.application.scheduler;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionPersistencePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.type.TransactionType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestProfile(SchedulerTestProfile.class)
@TestMethodOrder(OrderAnnotation.class)
@WithTestResource(H2DatabaseTestResource.class)
class SchedulerTest {

    @InjectMock
    TransactionServicePort transactionService;

    @InjectMock
    TransactionPersistencePort transactionRepository;

    @Inject
    Scheduler scheduler;

    @Order(1)
    @Test
    void processQueue_shouldFillTheQueue_whenQueueWasEmpty() {
        // Arrange
        when(transactionRepository.findUnprocessedTransactions()).thenReturn(new PriorityQueue<>());

        // Act
        scheduler.processQueue();

        // Assert
        verify(transactionRepository).findUnprocessedTransactions();
        verifyNoMoreInteractions(transactionRepository);
        verifyNoInteractions(transactionService);
    }

    @Order(2)
    @Test
    void processQueue_shouldProcessTransactionsInCorrectOrder_whenQueueIsFilled() {
        // Arrange
        Queue<Transaction> queue = getQueue();
        when(transactionRepository.findUnprocessedTransactions()).thenReturn(queue);

        // Expected ordered list of transaction IDs
        List<Long> expectedOrder = List.of(5L, 3L, 2L, 1L, 4L);

        // Act
        scheduler.processQueue();

        // Assert the processed order of transactions
        List<Long> actualOrder = queue.stream()
                .map(Transaction::getId)
                .toList();

        assertThat(actualOrder).containsExactlyElementsOf(expectedOrder);

        verify(transactionService, times(actualOrder.size())).processDigitalTransaction(any());
        verify(transactionRepository).findUnprocessedTransactions();
        verifyNoMoreInteractions(transactionRepository, transactionService);
    }

    private static Queue<Transaction> getQueue() {
        Queue<Transaction> queue = new PriorityQueue<>(Comparator.comparing(Transaction::getDate));

        queue.add(Transaction.builder()
                .id(1L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("250"))
                .label("First transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.parse("2022-12-08T00:00:00Z"))  // Fixed date for consistency
                .build());

        queue.add(Transaction.builder()
                .id(2L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("399"))
                .label("Second transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.parse("2022-12-07T10:14:00Z"))  // Fixed date
                .build());

        queue.add(Transaction.builder()
                .id(3L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("150"))
                .label("Third transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.parse("2022-12-06T10:14:00Z"))  // Fixed date
                .build());

        queue.add(Transaction.builder()
                .id(4L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("1000"))
                .label("Fourth transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.parse("2022-12-07T10:18:00Z"))  // Fixed date
                .build());

        queue.add(Transaction.builder()
                .id(5L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("59"))
                .label("Fifth transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.MIN)  // Oldest date for top priority
                .build());
        return queue;
    }
}
