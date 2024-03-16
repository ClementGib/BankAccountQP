package com.cdx.bas.application.bank.account;

import com.cdx.bas.application.mapper.DtoEntityMapper;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.type.TransactionType;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class BankAccountRepositoryTest {

    @Inject
    BankAccountRepository bankAccountRepository;
    
    @InjectMock
    DtoEntityMapper<BankAccount, BankAccountEntity> bankAccountMapper;

    @Test
    @Transactional
    public void shouldReturnAllBankAccounts_whenAccountsFound() {
        // Arrange
        Instant date = Instant.now();
        List<BankAccount> bankAccounts = List.of(createBankAccountUtils(1L, date), createBankAccountUtils(2L, date), createBankAccountUtils(3L, date));

        // Act
        List<BankAccount> allBankAccounts = bankAccountRepository.getAll();

        int size = allBankAccounts.size();
        // Assert
        assertThat(allBankAccounts).isNotEmpty();
        verify(bankAccountMapper, times(size)).toDto(any(BankAccountEntity.class));
        verifyNoMoreInteractions(bankAccountMapper);
    }


    @Test
    @Transactional
    public void shouldReturnBankAccount_whenAccountIsFound() {
        // Arrange
        long accountId = 1L;
        Instant date = Instant.now();
        BankAccount bankAccount = createBankAccountUtils(accountId, date);

        when(bankAccountMapper.toDto(any())).thenReturn(bankAccount);

        // Act
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(accountId);

        // Assert
        assertThat(optionalBankAccount).contains(bankAccount);
        verify(bankAccountMapper).toDto(any(BankAccountEntity.class));
        verifyNoMoreInteractions(bankAccountMapper);
    }
    
    @Test
    @Transactional
    public void shouldReturnEmptyOptional_whenAccountIsNotFound() {
        // Act
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(99999L);

        // Assert
        assertThat(optionalBankAccount).isEmpty();
        verifyNoInteractions(bankAccountMapper);
    }
    
    private BankAccount createBankAccountUtils(long accountId, Instant timestamp) {
        BankAccount bankAccount = new CheckingBankAccount();
        bankAccount.setId(accountId);
        bankAccount.setType(AccountType.CHECKING);
        bankAccount.setBalance(new Money(new BigDecimal("300")));
        Set<Long> customersId = new HashSet<>();
        customersId.add(1L);
        bankAccount.setCustomersId(customersId);
        Set<Transaction> transactionHistory = new HashSet<>();
        transactionHistory.add(Transaction.builder()
                .id(2L)
                .emitterAccountId(5000L)
                .receiverAccountId(77L)
                .amount(new BigDecimal(100))
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.ERROR)
                .date(timestamp)
                .label("transaction test")
                .metadata(Map.of("amount_before", "0", "amount_after", "350"))
                .build());
        bankAccount.setIssuedTransactions(transactionHistory);
        return bankAccount;
    }
}
