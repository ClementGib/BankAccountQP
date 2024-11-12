package com.cdx.bas.application.bank.transaction.type;

import com.cdx.bas.application.bank.transaction.category.cash.type.deposit.DepositProcessorImpl;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
class DepositProcessorServiceImplTest {

    @Inject
    BankAccountServicePort bankAccountService;

    @Inject
    DepositProcessorImpl depositProcessor;

    @Test
    @Transactional
    void should_debitReceiverAndDebitEmitter_when_transactionIsValidated() {
        // Arrange
        long emitterAccountId = 2L;
        Transaction transaction = new Transaction();
        transaction.setCurrency("EUR");
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setEmitterAccountId(emitterAccountId);
        transaction.setAmount(new BigDecimal("1000.00"));
        transaction.setStatus(TransactionStatus.UNPROCESSED);
        ZonedDateTime dateTime = ZonedDateTime.of(2024, 11, 6, 18, 0, 0, 0, ZoneOffset.ofHours(1));
        Instant transactionDate = dateTime.toInstant();
        transaction.setDate(transactionDate);
        transaction.setLabel("transaction 5");
        transaction.setMetadata(new HashMap<>());
        transaction.getMetadata().put("bill", "500,500");

        // Act
        Transaction actualTransaction = depositProcessor.processTransaction(transaction);

        // Assert
        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setEmitterAccountId(2L);
        expectedTransaction.setAmount(new BigDecimal("1000.00"));
        expectedTransaction.setCurrency("EUR");
        expectedTransaction.setType(TransactionType.DEPOSIT);
        expectedTransaction.setStatus(TransactionStatus.COMPLETED);
        expectedTransaction.setDate(Instant.parse("2024-11-06T17:00:00Z"));
        expectedTransaction.setLabel("transaction 5");
        Map<String, String> metadata = new HashMap<>();
        metadata.put("bill", "500,500");
        metadata.put("emitter_amount_after", "2600.00");
        metadata.put("emitter_amount_before", "1600.00");
        expectedTransaction.setMetadata(metadata);

        assertThat(actualTransaction)
                .usingRecursiveComparison()
                .isEqualTo(expectedTransaction);
        BankAccount emitterAccount = bankAccountService.findBankAccount(emitterAccountId);
        assertThat(emitterAccount.getBalance())
                .usingRecursiveComparison()
                .isEqualTo(Money.of(new BigDecimal("2600.00")));
    }
}