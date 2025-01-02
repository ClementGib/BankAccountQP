package com.cdx.bas.producer.generator;

import com.cdx.bas.domain.bank.transaction.category.NewDigitalTransaction;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {

    private Generator() {}

    public static NewDigitalTransaction generateTransaction() {
        int FIRST_ACCOUNT_ID = 1;
        int LAST_ACCOUNT_ID = 8;
        long randEmitterId = ThreadLocalRandom.current().nextLong(FIRST_ACCOUNT_ID, LAST_ACCOUNT_ID);
        long randReceiverId = 0;
        while (randReceiverId == 0 || randReceiverId == randEmitterId) {
            randReceiverId = ThreadLocalRandom.current().nextLong(FIRST_ACCOUNT_ID, LAST_ACCOUNT_ID);
        }
        Long emitterAccountId = randEmitterId;
        Long receiverAccountId = randReceiverId;
        TransactionType type = ThreadLocalRandom.current().nextBoolean() ? TransactionType.CREDIT : TransactionType.DEBIT;
        BigDecimal amount = getAmount(randEmitterId, randReceiverId, type
        );
        String currency = "EUR";
        String label = "demo transaction from " + emitterAccountId + " and " + receiverAccountId + " of " + amount;
        Map<String, String> metadata = Map.of();
        return new NewDigitalTransaction(emitterAccountId, receiverAccountId, amount, currency, type, label, metadata);
    }

    private static BigDecimal getAmount(long randEmitterId, long randReceiverId, TransactionType type) {
        final int LOWEST_AMOUNT = 10;
        int BIGGEST_AMOUNT = 400;
        final int[] richAccountIds = {5, 8};
        boolean isRich = Arrays.stream(richAccountIds)
                .anyMatch(accountId -> (accountId == randEmitterId && type == TransactionType.CREDIT)
                        || (accountId == randReceiverId && type == TransactionType.DEBIT));
        int multiplier = 1;
        if (isRich) {
            multiplier = 10;
        }
        long amount = ThreadLocalRandom.current().nextLong(LOWEST_AMOUNT, BIGGEST_AMOUNT * multiplier);
        return new BigDecimal(amount);
    }
}
