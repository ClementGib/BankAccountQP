package com.cdx.bas.client.bank.account;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.mma.MMABankAccount;
import com.cdx.bas.domain.bank.account.saving.SavingBankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static com.cdx.bas.domain.bank.transaction.type.TransactionType.CREDIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
class BankAccountResourceTest {

    @Inject
    BankAccountResource bankAccountResource;

    @Test
    public void getAll_shouldReturnAllBankAccount() {
        List<BankAccount> expectedCustomers = List.of(
                new CheckingBankAccount(1L, Money.of(new BigDecimal("400.00")), Set.of(1L), new HashSet<>()),
                new CheckingBankAccount(2L, Money.of(new BigDecimal("1600.00")), Set.of(2L, 3L), new HashSet<>()),
                new SavingBankAccount(3L, Money.of(new BigDecimal("19200.00")), Set.of(4L), new HashSet<>()),
                new CheckingBankAccount(4L, Money.of(new BigDecimal("500.00")), Set.of(3L), new HashSet<>()),
                new MMABankAccount(5L, Money.of(new BigDecimal("65000.00")), Set.of(1L), new HashSet<>()),
                new SavingBankAccount(6L, Money.of(new BigDecimal("999.00")), Set.of(5L), new HashSet<>()),
                new CheckingBankAccount(7L, Money.of(new BigDecimal("0.00")), Set.of(6L), new HashSet<>()),
                new SavingBankAccount(8L, Money.of(new BigDecimal("200000.00")), Set.of(6L), new HashSet<>())
        );

        List<BankAccount> actualTransactions = bankAccountResource.getAll();
        assertThat(actualTransactions)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("issuedTransactions")
                .isEqualTo(expectedCustomers);
    }

    @Test
    public void findById_shouldReturnBankAccount_whenBankAccountFound() {
        BankAccount expectedBankAccount = new SavingBankAccount();
        expectedBankAccount.setId(6L);
        expectedBankAccount.setBalance(Money.of(new BigDecimal("999.00")));
        expectedBankAccount.setCustomersId(Set.of(5L));

        BankAccount actualBankAccount = bankAccountResource.findById(6);
        assertThat(actualBankAccount)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("issuedTransactions")
                .isEqualTo(expectedBankAccount);
    }

    @Test
    public void findById_shouldReturnBankAccountWithTransactions_whenTransactionsFoundInBankAccount() {
        Transaction transaction1 = Transaction.builder()
                .id(3L)
                .emitterAccountId(6L)
                .receiverAccountId(3L)
                .amount(new BigDecimal("9200.00"))
                .currency("EUR")
                .type(CREDIT)
                .status(TransactionStatus.COMPLETED)
                .date(Instant.parse("2024-07-10T14:00:00Z"))
                .label("transaction 3")
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(2L)
                .emitterAccountId(6L)
                .receiverAccountId(3L)
                .amount(new BigDecimal("9200.00"))
                .currency("EUR")
                .type(CREDIT)
                .status(TransactionStatus.ERROR)
                .date(Instant.parse("2024-07-10T14:00:00Z"))
                .label("transaction 2")
                .build();
        List<Transaction> issuedTransaction = new ArrayList<>();
        issuedTransaction.add(transaction1);
        issuedTransaction.add(transaction2);

        BankAccount actualBankAccount = bankAccountResource.findById(6);

        assertThat(actualBankAccount.getIssuedTransactions())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .ignoringFields("metadata")
                .isEqualTo(issuedTransaction);

    }
    
    @Test
    public void findById_shouldReturnEmptyTransaction_whenTransactionNotFound() {
        try {
            bankAccountResource.findById(99L);
            fail();
        } catch (BankAccountException exception) {
            assertThat(exception.getMessage()).isEqualTo("Missing bank account with id: 99");
        }
    }
}