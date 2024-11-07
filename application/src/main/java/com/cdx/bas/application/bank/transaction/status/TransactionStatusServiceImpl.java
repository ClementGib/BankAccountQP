package com.cdx.bas.application.bank.transaction.status;

import com.cdx.bas.application.bank.transaction.TransactionRepository;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.message.MessageFormatter;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.OUTSTANDING;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static com.cdx.bas.domain.message.CommonMessages.*;

@RequestScoped
public class TransactionStatusServiceImpl implements TransactionStatusServicePort {

    TransactionRepository transactionPersistencePort;

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
        transaction.setMetadata(metadata);
        return transaction;
    }

    @Override
    @Transactional
    public Transaction saveStatus(Transaction transaction, TransactionStatus status, Map<String, String> metadata) throws TransactionException {
        return transactionPersistencePort.update(setStatus(transaction, status, metadata));
    }
}
