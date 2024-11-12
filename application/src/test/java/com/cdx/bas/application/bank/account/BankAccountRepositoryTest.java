package com.cdx.bas.application.bank.account;

import com.cdx.bas.application.bank.customer.CustomerRepository;
import com.cdx.bas.application.bank.transaction.TransactionRepository;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountPersistencePort;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.mma.MMABankAccount;
import com.cdx.bas.domain.bank.account.saving.SavingBankAccount;
import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.bank.customer.Customer;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
class BankAccountRepositoryTest {

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    TransactionRepository transactionRepository;

    @Test
    @Transactional
    @Order(1)
    void shouldReturnAllBankAccounts_whenAccountsFound() {
        // Arrange
        List<BankAccount> expectedAccounts = List.of(
                new CheckingBankAccount(1L, new Money(new BigDecimal("400.00")), null, null),
                new CheckingBankAccount(2L, new Money(new BigDecimal("1600.00")), null, null),
                new SavingBankAccount(3L, new Money(new BigDecimal("19200.00")), null, null),
                new CheckingBankAccount(4L, new Money(new BigDecimal("500.00")), null, null),
                new MMABankAccount(5L, new Money(new BigDecimal("65000.00")), null, null),
                new SavingBankAccount(6L, new Money(new BigDecimal("999.00")), null, null),
                new CheckingBankAccount(7L, new Money(new BigDecimal("0.00")), null, null),
                new SavingBankAccount(8L, new Money(new BigDecimal("200000.00")), null, null)
        );

        // Act
        List<BankAccount> allBankAccounts = bankAccountRepository.getAll();

        // Assert
        assertThat(allBankAccounts)
                .hasSize(expectedAccounts.size())
                .usingRecursiveComparison()
                .ignoringFields("customersId", "issuedTransactions", "incomingTransactions")
                .isEqualTo(expectedAccounts);
    }

    @Test
    @Transactional
    void shouldReturnBankAccount_whenAccountIsFound() {
        // Arrange
        long accountId = 1L;
        BankAccount bankAccount = new CheckingBankAccount(1L, new Money(new BigDecimal("400.00")), null, null);

        // Act
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(accountId);

        // Assert
        assertThat(optionalBankAccount).isPresent();
        assertThat(optionalBankAccount.get())
                .usingRecursiveComparison()
                .ignoringFields("customersId", "issuedTransactions", "incomingTransactions")
                .isEqualTo(bankAccount);
    }

    @Test
    @Transactional
    void shouldReturnEmptyOptional_whenAccountIsNotFound() {
        // Act
        Optional<BankAccount> optionalBankAccount = bankAccountRepository.findById(99999L);

        // Assert
        assertThat(optionalBankAccount).isEmpty();
    }


    @Test
    @Transactional
    @Order(2)
    void shouldCreateBankAccountSuccessfully() {
        // Arrange
        long id = 20L;
        BankAccount bankAccount = new CheckingBankAccount();
        bankAccount.setType(AccountType.CHECKING);
        bankAccount.setBalance(new Money(new BigDecimal("0")));
        bankAccount.setCustomersId(Set.of(1L));

        // Act
        bankAccountRepository.create(bankAccount);

        // Assert
        Optional<BankAccount> createdBankAccount = bankAccountRepository.findById(id);
        bankAccount.setId(id);
        assertThat(createdBankAccount).isPresent();
        assertThat(createdBankAccount.get())
                .usingRecursiveComparison()
                .isEqualTo(bankAccount);
    }

    @Test
    @Transactional
    @Order(3)
    void shouldUpdateBankAccountSuccessfully() {
        // Arrange
        long id = 8L;
        Instant timestamp = Instant.now();
        BankAccount bankAccount = new CheckingBankAccount();
        bankAccount.setId(id);
        bankAccount.setType(AccountType.SAVING);
        bankAccount.setBalance(new Money(new BigDecimal("200000.00")));
        bankAccount.setCustomersId(Set.of(6L));
        Transaction transaction = new Transaction();
        transaction.setEmitterAccountId(id);
        transaction.setAmount(new BigDecimal("5000.00"));
        transaction.setCurrency("EUR");
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.UNPROCESSED);
        transaction.setDate(timestamp);
        transaction.setLabel("transaction 8");
        transaction.setMetadata(Map.of("bill", "2500,2500"));
        bankAccount.getIssuedTransactions().add(transaction);


        // Act
        BankAccount updated = bankAccountRepository.update(bankAccount);

        // Assert
        Optional<BankAccount> updatedAccount = ((BankAccountPersistencePort) bankAccountRepository).findById(updated.getId());
        transaction.setId(updated.getIssuedTransactions().iterator().next().getId());
        assertThat(updatedAccount).isPresent();
        assertThat(updatedAccount.get())
                .usingRecursiveComparison()
                .isEqualTo(bankAccount);
    }

    @Test
    @Transactional
    @Order(4)
    void shouldDeleteBankAccountSuccessfully_whenAccountExists() {
        // Act
        long id = 3L;

        // Arrange
        Optional<BankAccount> deletedAccount = bankAccountRepository.deleteById(id);

        // Assert
        assertThat(deletedAccount).isNotEmpty();
        Optional<Customer> customer = customerRepository.findById(4L);
        assertThat(customer).isPresent();
        assertThat(bankAccountRepository.findById(id)).isEmpty();
    }

    @Test
    @Transactional
    void shouldReturnEmptyOptional_whenDeletingNonExistentAccount() {
        // Act
        Optional<BankAccount> deletedAccount = bankAccountRepository.deleteById(99999L);

        // Assert
        assertThat(deletedAccount).isEmpty();
    }

    @Test
    @Transactional
    void deleteById_shouldReturnEmpty_whenIdDoesNotExist() {
        // Act
        Optional<BankAccount> deletedBankAccount = bankAccountRepository.deleteById(999L);

        // Assert
        assertThat(deletedBankAccount).isEmpty();
    }
}
