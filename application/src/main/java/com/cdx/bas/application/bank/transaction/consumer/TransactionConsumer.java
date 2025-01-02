package com.cdx.bas.application.bank.transaction.consumer;

import com.cdx.bas.domain.bank.transaction.TransactionPersistencePort;
import com.cdx.bas.domain.bank.transaction.id.IdPair;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.id.IdUtils;
import com.cdx.bas.domain.consumer.Consumer;
import io.quarkus.runtime.Startup;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static com.cdx.bas.domain.bank.transaction.id.IdUtils.createIdPair;

@Startup
@Singleton
public class TransactionConsumer implements Consumer<Transaction> {

    private static final Logger logger = Logger.getLogger(TransactionConsumer.class);
    private final ConcurrentHashMap<IdPair, BlockingQueue<Transaction>> queueByPair = new ConcurrentHashMap<>();
    private final Set<IdPair> processingSet = ConcurrentHashMap.newKeySet();
    private final Thread workerThread;

    private final TransactionServicePort transactionService;
    private final TransactionPersistencePort transactionRepository;

    @ConfigProperty(name = "consumer.activation", defaultValue = "true")
    boolean activation;

    @ConfigProperty(name = "consumer.thread.max", defaultValue = "5")
    int maxThread;

    public TransactionConsumer(TransactionServicePort transactionService, TransactionPersistencePort transactionRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        transactionRepository.findUnprocessedTransactions().forEach(this::add);
        workerThread = new Thread(this::consume);
        workerThread.setDaemon(true);
        workerThread.start();
    }

    public boolean isActivated() {
        return activation;
    }

    @Override
    public void add(Transaction transaction) {
        logger.debug("Adding transaction: " + transaction);
        IdPair idPair = createIdPair(transaction);
        if (queueByPair.containsKey(idPair)) {
            BlockingQueue<Transaction> queue = new LinkedBlockingQueue<>();
            queue.add(transaction);
            queueByPair.put(idPair, queue);
        } else {
            queueByPair.get(idPair).add(transaction);
        }
        synchronized (this) {
            notify();
        }
    }

    private void consume() {
        while (isActivated()) {
            try {
                Transaction transaction = null;
                IdPair idPair = null;

                synchronized (this) {
                    while (queueByPair.isEmpty()) {
                        wait();
                    }

                    for (IdPair pair : queueByPair.keySet()) {
                        if (!processingSet.contains(pair)) {
                            idPair = pair;
                            transaction = queueByPair.get(pair).poll();
                            if (transaction != null) {
                                break;
                            }
                        }
                    }

                    if (transaction == null) {
                        continue;
                    }

                    processingSet.add(idPair);
                    processTransaction(transaction, idPair);
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void processTransaction(Transaction transaction, IdPair idPair) {
        try {
            logger.info("Processing transaction: " + transaction);
            transactionService.processDigitalTransaction(transaction);
        } finally {
            synchronized (this) {
                processingSet.remove(idPair);

                if (queueByPair.get(idPair) != null && !queueByPair.get(idPair).isEmpty()) {
                    notify();
                }
            }
        }
    }
}
