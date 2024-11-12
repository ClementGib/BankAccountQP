package com.cdx.bas.application.bank.transaction.status;

import com.cdx.bas.application.bank.transaction.TransactionRepository;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.message.MessageFormatter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.OUTSTANDING;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.metadata.MetadataFieldNames.ERROR_KEY;

@ApplicationScoped
public class TransactionStatusServiceImpl implements TransactionStatusServicePort {

    TransactionRepository transactionPersistencePort;

    public TransactionStatus handleError(Exception exception, Map<String, String> metadata) {
        if (exception instanceof NoSuchElementException) {
            metadata.put(ERROR_KEY, exception.getMessage());
            return TransactionStatus.ERROR;
        } else {
            metadata.put(ERROR_KEY, exception.getMessage());
            return TransactionStatus.REFUSED;
        }
    }

    @Inject
    public TransactionStatusServiceImpl(TransactionRepository transactionPersistencePort) {
        this.transactionPersistencePort = transactionPersistencePort;
    }

    @Override
    @Transactional
    public Transaction setAsOutstanding(Transaction transaction) throws TransactionException {
        if (UNPROCESSED.equals(transaction.getStatus())) {
            transaction.setStatus(OUTSTANDING);
        } else {
            throw new TransactionException(MessageFormatter.format(TRANSACTION_CONTEXT, OUTSTANDING_STATUS_ACTION, NO_LONGER_UNPROCESSED_STATUS,
                    List.of(STATUS_DETAIL + transaction.getStatus())));
        }
        return transactionPersistencePort.update(transaction);
    }

    @Override
    @Transactional
    public Transaction setStatus(Transaction transaction, TransactionStatus status, Map<String, String> metadata) {
        if (transaction == null) {
            throw new TransactionException(MessageFormatter.format(TRANSACTION_CONTEXT, CHANGE_STATUS_ACTION, IS_NULL_STATUS));
        }
        transaction.setStatus(status);
        transaction.getMetadata().putAll(metadata);
        return transaction;
    }

    @Override
    @Transactional
    public Transaction saveStatus(Transaction transaction, TransactionStatus status, Map<String, String> metadata) throws TransactionException {
        return transactionPersistencePort.update(setStatus(transaction, status, metadata));
    }
}
