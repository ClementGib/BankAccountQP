package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.exception.DomainException;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.*;
import static com.cdx.bas.domain.message.CommonMessages.FAILED_STATUS;
import static com.cdx.bas.domain.message.CommonMessages.REFUSED_STATUS;
import static com.cdx.bas.domain.metadata.MetadataFieldNames.ERROR_KEY;

@NoArgsConstructor
public abstract class TransactionProcessorTemplate {

    protected abstract Transaction processCategory(Transaction transaction, Map<String, String> metadata);
    protected abstract void persist(Transaction transaction);
    protected abstract String formatError(Transaction transaction, String errorStatus, Exception exception);

    @Transactional
    public Transaction processTransaction(Transaction transaction) {
        Map<String, String> metadata = new HashMap<>();
        TransactionStatus transactionStatus = COMPLETED;
        try {
            return processCategory(transaction, metadata);
        } catch (NoSuchElementException exception) {
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = ERROR;
            throw new TransactionException(formatError(transaction, FAILED_STATUS, exception));
        } catch (DomainException exception) {
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new TransactionException(formatError(transaction, REFUSED_STATUS, exception));
        } finally {
            transaction.setStatus(transactionStatus);
            transaction.getMetadata().putAll(metadata);
            persist(transaction);
        }
    }
}
