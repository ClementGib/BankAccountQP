package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.category.NewCashTransaction;
import com.cdx.bas.domain.bank.transaction.category.NewDigitalTransaction;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import lombok.experimental.UtilityClass;

import java.time.Clock;

@UtilityClass
public class TransactionUtils {

    private static final Clock clock = Clock.systemDefaultZone();

    public static Transaction getNewDigitalTransaction(NewDigitalTransaction newDigitalTransaction) {
        Transaction transaction = new Transaction();
        transaction.setEmitterAccountId(newDigitalTransaction.emitterAccountId());
        transaction.setReceiverAccountId(newDigitalTransaction.receiverAccountId());
        transaction.setAmount(newDigitalTransaction.amount());
        transaction.setCurrency(newDigitalTransaction.currency());
        transaction.setType(newDigitalTransaction.type());
        transaction.setStatus(TransactionStatus.UNPROCESSED);  // Assuming UNPROCESSED is an enum constant
        transaction.setDate(clock.instant());
        transaction.setLabel(newDigitalTransaction.label());
        transaction.setMetadata(newDigitalTransaction.metadata());
        return transaction;

    }

    public static Transaction getNewCashTransaction(NewCashTransaction newCashTransaction) {
        Transaction transaction = new Transaction();
        transaction.setEmitterAccountId(newCashTransaction.emitterAccountId());
        transaction.setAmount(newCashTransaction.amount());
        transaction.setCurrency(newCashTransaction.currency());
        if (newCashTransaction.metadata() != null) {
            transaction.setMetadata(newCashTransaction.metadata());
        }
        transaction.setDate(clock.instant());
        transaction.setStatus(TransactionStatus.UNPROCESSED);  // Assuming UNPROCESSED is an enum constant
        return transaction;
    }

    public static Transaction mergeTransactions(Transaction oldTransaction, Transaction newTransaction) {
        oldTransaction.setId(newTransaction.getId());
        oldTransaction.setEmitterAccountId(newTransaction.getEmitterAccountId());
        oldTransaction.setReceiverAccountId(newTransaction.getReceiverAccountId());
        oldTransaction.setAmount(newTransaction.getAmount());
        oldTransaction.setCurrency(newTransaction.getCurrency());
        oldTransaction.setType(newTransaction.getType());
        oldTransaction.setStatus(newTransaction.getStatus());
        oldTransaction.setDate(newTransaction.getDate());
        oldTransaction.setLabel(newTransaction.getLabel());
        oldTransaction.setMetadata(newTransaction.getMetadata());
        return oldTransaction;
    }
}
