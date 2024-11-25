package com.cdx.bas.application.scheduler;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionPersistencePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.PriorityQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.wildfly.common.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
class SchedulerImplTest {

    @Mock
    TransactionServicePort transactionService;

    @Mock
    TransactionPersistencePort transactionRepository;

    @InjectMocks
    SchedulerImpl scheduler;

    @Test
    void testProcessQueue_withActivatedScheduler() {
        // Arrange
        scheduler.activation = true;
        PriorityQueue<Transaction> mockQueue = new PriorityQueue<>();
        Transaction transaction1 = mock(Transaction.class);
        Transaction transaction2 = mock(Transaction.class);
        mockQueue.add(transaction1);
        mockQueue.add(transaction2);

        PriorityQueue<Transaction> transactions = new PriorityQueue<>() {{
            add(transaction1);
            add(transaction2);
        }};

        when(transactionRepository.findUnprocessedTransactions()).thenReturn(transactions);

        // Act
        scheduler.processQueue();

        // Assert
        verify(transactionRepository).findUnprocessedTransactions();
        verify(transactionService, times(1)).processDigitalTransaction(transaction1);
        verify(transactionService, times(1)).processDigitalTransaction(transaction2);
        assertThat(getTransactionQueueReflection()).isEmpty();
    }

    @Test
    void testProcessQueue_withDeactivatedScheduler() {
        // Arrange
        scheduler.activation = false;

        // Act
        scheduler.processQueue();

        // Assert
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(transactionService);
    }

    @Test
    void testIsActivated() {
        // Arrange
        scheduler.activation = true;

        // Act
        boolean activated = scheduler.isActivated();

        // Assert
        assertTrue(activated);

        // Arrange (deactivated scenario)
        scheduler.activation = false;

        // Act
        boolean deactivated = scheduler.isActivated();

        // Assert
        assertThat(deactivated).isFalse();
    }

    private PriorityQueue<Transaction> getTransactionQueueReflection() {
        try {
            Field field = SchedulerImpl.class.getDeclaredField("transactionQueue");
            field.setAccessible(true);
            return (PriorityQueue<Transaction>) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Reflection failed for transactionQueue", e);
        }
    }
}
