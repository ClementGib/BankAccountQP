package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.NewCashTransaction;
import com.cdx.bas.domain.bank.transaction.NewDigitalTransaction;
import com.cdx.bas.domain.bank.transaction.Transaction;
import lombok.experimental.UtilityClass;

import java.time.Clock;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;

@UtilityClass
public class TransactionUtils {

    private static final Clock clock = Clock.systemDefaultZone();

    public static Transaction getNewDigitalTransaction(NewDigitalTransaction newDigitalTransaction) {
        return Transaction.builder()
                .emitterAccountId(newDigitalTransaction.emitterAccountId())
                .receiverAccountId(newDigitalTransaction.receiverAccountId())
                .amount(newDigitalTransaction.amount())
                .currency(newDigitalTransaction.currency())
                .type(newDigitalTransaction.type())
                .status(UNPROCESSED)
                .date(clock.instant())
                .label(newDigitalTransaction.label())
                .metadata(newDigitalTransaction.metadata())
                .build();
    }

    public static Transaction getNewCashTransaction(NewCashTransaction newCashTransaction) {
        return Transaction.builder()
                .emitterAccountId(newCashTransaction.emitterAccountId())
                .amount(newCashTransaction.amount())
                .currency(newCashTransaction.currency())
                .metadata(newCashTransaction.metadata())
                .date(clock.instant())
                .status(UNPROCESSED)
                .build();
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
