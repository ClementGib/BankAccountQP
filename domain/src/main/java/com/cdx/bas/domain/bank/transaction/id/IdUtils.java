package com.cdx.bas.domain.bank.transaction.id;

import com.cdx.bas.domain.bank.transaction.Transaction;

import java.util.Enumeration;

public class IdUtils {
    public static IdPair createIdPair(Transaction transaction) {
        return new IdPair(transaction.getEmitterAccountId(), transaction.getReceiverAccountId());
    }

    public static boolean containsKey(IdPair idPair, Enumeration<IdPair> keys) {
        for (IdPair key : keys.) {

        }
    }
}
