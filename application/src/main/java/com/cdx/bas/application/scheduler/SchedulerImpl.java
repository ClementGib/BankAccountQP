package com.cdx.bas.application.scheduler;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionPersistencePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.message.MessageFormatter;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.PriorityQueue;

import static com.cdx.bas.domain.message.CommonMessages.*;

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
            logger.debug(MessageFormatter.format(SCHEDULER_CONTEXT, STARTING_ACTION, DONE_STATUS));
            if (getTransactionQueue().isEmpty()) {
                getTransactionQueue().addAll(transactionRepository.findUnprocessedTransactions());
            }

            logger.debug(MessageFormatter.format(SCHEDULER_CONTEXT, PROCESS_ACTION, IN_PROGRESS_STATUS, List.of(QUEUE_DETAIL + transactionQueue.size())));
            while (!getTransactionQueue().isEmpty()) {
                Transaction currentTransation = getTransactionQueue().poll();
                transactionService.processDigitalTransaction(currentTransation);
            }
            logger.debug(MessageFormatter.format(SCHEDULER_CONTEXT, ENDING_ACTION, DONE_STATUS));
        }
    }
    public boolean isActivated() {
        return activation;
    }
}