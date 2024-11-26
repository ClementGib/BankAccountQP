package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.application.bank.account.BankAccountEntity;
import com.cdx.bas.application.bank.account.BankAccountRepository;
import com.cdx.bas.application.bank.customer.CustomerEntity;
import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
class TransactionMapperTest {

    @InjectMock
    private BankAccountRepository bankAccountRepository;

    @Inject
    TransactionMapper transactionMapper;

    @Test
    void toDto_shouldMapEmpty_whenTransactionEntityIsEmpty() {
        Transaction actualDto = transactionMapper.toDto(new TransactionEntity());

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setMetadata(new HashMap<>());
        assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedTransaction);
    }

    @Test
    void toEntity_shouldMapEmpty_whenTransactionDtoIsEmpty() {
        TransactionEntity actualEntity = transactionMapper.toEntity(new Transaction());

        TransactionEntity expectedTransactionEntity = new TransactionEntity();
        expectedTransactionEntity.setMetadata("{}");
        assertThat(actualEntity).isEqualTo(expectedTransactionEntity);
    }

    @Test
    void toDto_shouldMapEntityValues_whenEntityHasValues() {
        Instant timestamp = Instant.now();
        long emitterAccountId = 99L;
        long receiverAccountId = 77L;
        Map<String, String> metadata = Map.of("amount_before", "0", "amount_after", "100");
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setId(10L);
        transactionEntity.setEmitterBankAccountEntity(createBankAccountEntity(emitterAccountId));
        transactionEntity.setReceiverBankAccountEntity(createBankAccountEntity(receiverAccountId));
        transactionEntity.setAmount(new BigDecimal("100"));
        transactionEntity.setCurrency("EUR");
        transactionEntity.setType(TransactionType.CREDIT);
        transactionEntity.setStatus(TransactionStatus.COMPLETED);
        transactionEntity.setDate(timestamp);
        transactionEntity.setLabel("transaction test");
        transactionEntity.setMetadata("{\"amount_after\" : \"100\", \"amount_before\" : \"0\"}");

        Transaction dto = transactionMapper.toDto(transactionEntity);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getEmitterAccountId()).isEqualTo(emitterAccountId);
        assertThat(dto.getReceiverAccountId()).isEqualTo(receiverAccountId);
        assertThat(dto.getAmount()).isEqualTo(new BigDecimal("100"));
        assertThat(dto.getCurrency()).isEqualTo("EUR");
        assertThat(dto.getType()).isEqualTo(TransactionType.CREDIT);
        assertThat(dto.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(dto.getDate()).isEqualTo(timestamp);
        assertThat(dto.getLabel()).hasToString("transaction test");
        assertThat(dto.getMetadata()).usingRecursiveComparison().isEqualTo(metadata);
    }

    @Test
    void toEntity_shouldMapEntityValues_whenDtoHasValues() {
        Instant timestamp = Instant.now();
        long emitterAccountId = 99L;
        long receiverAccountId = 77L;

        Map<String, String> metadata = Map.of("amount_before", "0", "amount_after", "100");
        BankAccountEntity emitterBankAccountEntity = createBankAccountEntity(emitterAccountId);
        BankAccountEntity receiverBankAccountEntity = createBankAccountEntity(receiverAccountId);
        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setEmitterAccountId(emitterAccountId);
        transaction.setReceiverAccountId(receiverAccountId);
        transaction.setAmount(new BigDecimal(100));
        transaction.setCurrency("EUR");
        transaction.setType(TransactionType.CREDIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDate(timestamp);
        transaction.setLabel("transaction test");
        transaction.setMetadata(metadata);

        when(bankAccountRepository.findByIdOptional(99L)).thenReturn(Optional.of(emitterBankAccountEntity));
        when(bankAccountRepository.findByIdOptional(77L)).thenReturn(Optional.of(receiverBankAccountEntity));

        TransactionEntity entity = transactionMapper.toEntity(transaction);

        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getEmitterBankAccountEntity()).usingRecursiveComparison().isEqualTo(emitterBankAccountEntity);
        assertThat(entity.getReceiverBankAccountEntity()).usingRecursiveComparison().isEqualTo(receiverBankAccountEntity);
        assertThat(entity.getAmount()).usingRecursiveComparison().isEqualTo(new BigDecimal(100));
        assertThat(entity.getCurrency()).isEqualTo("EUR");
        assertThat(entity.getType()).isEqualTo(TransactionType.CREDIT);
        assertThat(entity.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(entity.getDate()).isEqualTo(timestamp);
        assertThat(entity.getLabel()).hasToString("transaction test");
        String amountBefore = "\"amount_before\":\"0\"";
        String amountAfter = "\"amount_after\":\"100\"";
        assertThat(entity.getMetadata())
                .contains(amountBefore)
                .contains(amountAfter);
    }

    private BankAccountEntity createBankAccountEntity(long id) {
        BankAccountEntity bankAccountEntity = new BankAccountEntity();
        bankAccountEntity.setId(id);
        bankAccountEntity.setType(AccountType.CHECKING);
        bankAccountEntity.setBalance(new BigDecimal("100"));
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(99L);
        bankAccountEntity.setCustomers(Set.of(customerEntity));
        return bankAccountEntity;
    }
}