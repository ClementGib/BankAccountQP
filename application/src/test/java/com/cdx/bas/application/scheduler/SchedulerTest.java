package com.cdx.bas.application.scheduler;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionPersistencePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
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
    }

    private static Queue<Transaction> getQueue() {
        Queue<Transaction> queue = new PriorityQueue<>(Comparator.comparing(Transaction::getDate));
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setEmitterAccountId(99L);
        transaction1.setReceiverAccountId(77L);
        transaction1.setAmount(new BigDecimal("250"));
        transaction1.setLabel("First transaction");
        transaction1.setType(TransactionType.CREDIT);
        transaction1.setStatus(TransactionStatus.UNPROCESSED);
        transaction1.setDate(Instant.parse("2022-12-08T00:00:00Z"));
        queue.add(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setEmitterAccountId(99L);
        transaction2.setReceiverAccountId(77L);
        transaction2.setAmount(new BigDecimal("399"));
        transaction2.setLabel("Second transaction");
        transaction2.setType(TransactionType.CREDIT);
        transaction2.setStatus(TransactionStatus.UNPROCESSED);
        transaction2.setDate(Instant.parse("2022-12-07T10:14:00Z"));
        queue.add(transaction2);

        Transaction transaction3 = new Transaction();
        transaction3.setId(3L);
        transaction3.setEmitterAccountId(99L);
        transaction3.setReceiverAccountId(77L);
        transaction3.setAmount(new BigDecimal("150"));
        transaction3.setLabel("Third transaction");
        transaction3.setType(TransactionType.CREDIT);
        transaction3.setStatus(TransactionStatus.UNPROCESSED);
        transaction3.setDate(Instant.parse("2022-12-06T10:14:00Z"));
        queue.add(transaction3);

        Transaction transaction4 = new Transaction();
        transaction4.setId(4L);
        transaction4.setEmitterAccountId(99L);
        transaction4.setReceiverAccountId(77L);
        transaction4.setAmount(new BigDecimal("1000"));
        transaction4.setLabel("Fourth transaction");
        transaction4.setType(TransactionType.CREDIT);
        transaction4.setStatus(TransactionStatus.UNPROCESSED);
        transaction4.setDate(Instant.parse("2022-12-07T10:18:00Z"));
        queue.add(transaction4);

        Transaction transaction5 = new Transaction();
        transaction5.setId(5L);
        transaction5.setEmitterAccountId(99L);
        transaction5.setReceiverAccountId(77L);
        transaction5.setAmount(new BigDecimal("59"));
        transaction5.setLabel("Fifth transaction");
        transaction5.setType(TransactionType.CREDIT);
        transaction5.setStatus(TransactionStatus.UNPROCESSED);
        transaction5.setDate(Instant.MIN); // Oldest date for top priority
        queue.add(transaction5);
        return queue;
    }
}
