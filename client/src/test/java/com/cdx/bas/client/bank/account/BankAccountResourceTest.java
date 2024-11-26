package com.cdx.bas.client.bank.account;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.mma.MMABankAccount;
import com.cdx.bas.domain.bank.account.saving.SavingBankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankAccountResourceTest {

    @Inject
    BankAccountResource bankAccountResource;

    @Test
    @Order(1)
    void getAll_shouldReturnAllBankAccount() {
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
                .ignoringFields("issuedTransactions", "incomingTransactions")
                .isEqualTo(expectedCustomers);
    }

    @Test
    void findById_shouldReturnBankAccount_whenBankAccountFound() {
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
    void findById_shouldReturnBankAccountWithTransactions_whenTransactionsFoundInBankAccount() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(3L);
        transaction1.setEmitterAccountId(6L);
        transaction1.setReceiverAccountId(3L);
        transaction1.setAmount(new BigDecimal("9200.00"));
        transaction1.setCurrency("EUR");
        transaction1.setType(TransactionType.CREDIT); // Assurez-vous que TransactionType.CREDIT est défini
        transaction1.setStatus(TransactionStatus.COMPLETED); // Assurez-vous que TransactionStatus.COMPLETED est défini
        transaction1.setDate(Instant.parse("2024-07-10T14:00:00Z"));
        transaction1.setLabel("transaction 3");

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setEmitterAccountId(6L);
        transaction2.setReceiverAccountId(3L);
        transaction2.setAmount(new BigDecimal("9200.00"));
        transaction2.setCurrency("EUR");
        transaction2.setType(TransactionType.CREDIT);
        transaction2.setStatus(TransactionStatus.ERROR); // Assurez-vous que TransactionStatus.ERROR est défini
        transaction2.setDate(Instant.parse("2024-07-10T14:00:00Z"));
        transaction2.setLabel("transaction 2");

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
    void findById_shouldReturnEmptyTransaction_whenTransactionNotFound() {
        try {
            bankAccountResource.findById(99L);
            fail();
        } catch (BankAccountException exception) {
            String expectedMessage = "Bank account: searching failed - not found\nBank account id:99";
            assertThat(exception.getMessage()).isEqualTo(expectedMessage);
        }
    }
}