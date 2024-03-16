package com.cdx.bas.application.scheduler;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionPersistencePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.PriorityQueue;

@Startup
@Singleton
public class SchedulerImpl implements Scheduler {

    private static final Logger logger = Logger.getLogger(SchedulerImpl.class);

    private static final PriorityQueue<Transaction> transactionQueue = new PriorityQueue<>();

    private final TransactionServicePort transactionService;
    private final TransactionPersistencePort transactionRepository;

    @Inject
    public SchedulerImpl(TransactionPersistencePort transactionRepository, TransactionServicePort transactionService) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    @ConfigProperty(name = "scheduler.activation", defaultValue = "true")
    boolean activation;

    @ConfigProperty(name = "scheduler.every", defaultValue = "30s")
    String every;

    private static PriorityQueue<Transaction> getTransactionQueue() {
        return transactionQueue;
    }

    @Override
    @Scheduled(every = "{scheduler.every}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void processQueue() {
        if (isActivated()) {
            logger.info("Scheduler start every " + getEvery());
            if (getTransactionQueue().isEmpty()) {
                getTransactionQueue().addAll(transactionRepository.findUnprocessedTransactions());
            }

            logger.info("Queue size: " + transactionQueue.size());
            while (!getTransactionQueue().isEmpty()) {
                Transaction currentTransation = getTransactionQueue().poll();
                transactionService.processDigitalTransaction(currentTransation);
            }
            logger.info("Scheduler end");
        }
    }

    public boolean isActivated() {
        return activation;
    }

    public String getEvery() {
        return every;
    }
}