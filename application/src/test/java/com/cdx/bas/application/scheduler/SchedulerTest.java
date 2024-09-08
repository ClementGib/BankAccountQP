package com.cdx.bas.application.scheduler;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionPersistencePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.type.TransactionType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@QuarkusTest
@TestProfile(SchedulerTestProfile.class)
@TestMethodOrder(OrderAnnotation.class)
public class SchedulerTest {

    @InjectMock
    TransactionServicePort transactionService;

    @InjectMock
    TransactionPersistencePort transactionRepository;

    @Inject
    Scheduler scheduler;

    @Order(1)
    @Test
    public void processQueue_shouldFillTheQueue_whenQueueWasEmpty() {
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
    public void processQueue_shouldRunSchedulerProcess_withOrderedQueues_whenQueueIsFilled_withUnprocessedTransactions() {
        // Arrange
        Queue<Transaction> queue = createCreditTransactionsUtils();
        when(transactionRepository.findUnprocessedTransactions()).thenReturn(queue);
        Clock clock;

        Transaction fifthTransaction = Transaction.builder()
                .id(5L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("59"))
                .label("Fifth transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.MIN)
                .build();
        clock = Clock.fixed(Instant.parse("2022-12-06T10:14:00Z"), ZoneId.of("UTC"));
        Transaction thirdTransaction = Transaction.builder()
                .id(3L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("150"))
                .label("Third transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now(clock))
                .build();
        clock = Clock.fixed(Instant.parse("2022-12-07T10:14:00Z"), ZoneId.of("UTC"));
        Transaction secondTransaction = Transaction.builder()
                .id(2L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("399"))
                .label("Second transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now(clock))
                .build();
        clock = Clock.fixed(Instant.parse("2022-12-07T10:18:00Z"), ZoneId.of("UTC"));
        Transaction fourthTransaction = Transaction.builder()
                .id(4L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("1000"))
                .label("Fourth transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now(clock))
                .build();

        // Act
        scheduler.processQueue();

        // Assert
        assertThat(queue.peek()).usingRecursiveComparison().isEqualTo(fifthTransaction);
        verify(transactionService).processDigitalTransaction(queue.poll());
        assertThat(queue.peek()).usingRecursiveComparison().isEqualTo(thirdTransaction);
        verify(transactionService).processDigitalTransaction(queue.poll());
        assertThat(queue.peek()).usingRecursiveComparison().isEqualTo(secondTransaction);
        verify(transactionService).processDigitalTransaction(queue.poll());
        assertThat(queue.peek()).usingRecursiveComparison().isEqualTo(fourthTransaction);
        verify(transactionService).processDigitalTransaction(queue.poll());

        verify(transactionRepository).findUnprocessedTransactions();
        verify(transactionService, times(5)).processDigitalTransaction(any());
        verifyNoMoreInteractions(transactionRepository, transactionService);
    }

    private static Queue<Transaction> createCreditTransactionsUtils() {
        Clock clock;
        Queue<Transaction> queue = new PriorityQueue<>();
        queue.add(Transaction.builder()
                .id(1L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("250"))
                .label("First transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.MAX)
                .build());
        clock = Clock.fixed(Instant.parse("2022-12-07T10:14:00Z"), ZoneId.of("UTC"));
        queue.add(Transaction.builder()
                .id(2L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("399"))
                .label("Second transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now(clock))
                .build());
        clock = Clock.fixed(Instant.parse("2022-12-06T10:14:00Z"), ZoneId.of("UTC"));
        queue.add(Transaction.builder()
                .id(3L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("150"))
                .label("Third transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now(clock))
                .build());
        clock = Clock.fixed(Instant.parse("2022-12-07T10:18:00Z"), ZoneId.of("UTC"));
        queue.add(Transaction.builder()
                .id(4L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("1000"))
                .label("Fourth transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.now(clock))
                .build());
        queue.add(Transaction.builder()
                .id(5L)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("59"))
                .label("Fifth transaction")
                .type(TransactionType.CREDIT)
                .status(UNPROCESSED)
                .date(Instant.MIN)
                .build());
        return queue;
    }
}