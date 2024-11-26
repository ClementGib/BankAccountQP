package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    @Test
    void testCompareTo() {
        // Arrange
        Instant now = Instant.now();
        Instant earlier = now.minusSeconds(3600); // 1 hour earlier
        Instant later = now.plusSeconds(3600);    // 1 hour later

        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setEmitterAccountId(101L);
        transaction1.setReceiverAccountId(102L);
        transaction1.setAmount(new BigDecimal("100"));
        transaction1.setCurrency("USD");
        transaction1.setType(TransactionType.CREDIT);
        transaction1.setStatus(TransactionStatus.UNPROCESSED);
        transaction1.setDate(now);
        transaction1.setLabel("Transaction 1");
        transaction1.setMetadata(new HashMap<>());

        Transaction transactionEarlier = new Transaction();
        transactionEarlier.setId(2L);
        transactionEarlier.setEmitterAccountId(101L);
        transactionEarlier.setReceiverAccountId(102L);
        transactionEarlier.setAmount(new BigDecimal("200"));
        transactionEarlier.setCurrency("USD");
        transactionEarlier.setType(TransactionType.DEBIT);
        transactionEarlier.setStatus(TransactionStatus.UNPROCESSED);
        transactionEarlier.setDate(earlier);
        transactionEarlier.setLabel("Transaction Earlier");
        transactionEarlier.setMetadata(new HashMap<>());

        Transaction transactionLater = new Transaction();
        transactionLater.setId(3L);
        transactionLater.setEmitterAccountId(101L);
        transactionLater.setReceiverAccountId(102L);
        transactionLater.setAmount(new BigDecimal("300"));
        transactionLater.setCurrency("USD");
        transactionLater.setType(TransactionType.CREDIT);
        transactionLater.setStatus(TransactionStatus.UNPROCESSED);
        transactionLater.setDate(later);
        transactionLater.setLabel("Transaction Later");
        transactionLater.setMetadata(new HashMap<>());


        // Act & Assert
        // transaction1 is later than transactionEarlier
        assertTrue(transaction1.compareTo(transactionEarlier) > 0, "transaction1 should be after transactionEarlier");

        // transaction1 is earlier than transactionLater
        assertTrue(transaction1.compareTo(transactionLater) < 0, "transaction1 should be before transactionLater");

        // transaction1 is equal to itself
        assertEquals(0, transaction1.compareTo(transaction1), "transaction1 should be equal to itself");
    }
}