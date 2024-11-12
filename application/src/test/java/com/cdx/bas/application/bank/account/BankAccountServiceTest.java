package com.cdx.bas.application.bank.account;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.mma.MMABankAccount;
import com.cdx.bas.domain.bank.account.saving.SavingBankAccount;
import com.cdx.bas.domain.bank.account.type.AccountType;
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankAccountServiceTest {

    @Inject
    BankAccountServicePort bankAccountService;

    @Test
    @Order(1)
    void shouldGetAllBankAccounts_whenRepositoryFoundBankAccounts() {
        // Arrange
        List<BankAccount> bankAccounts = getAllBankAccounts();

        // Act
        List<BankAccount> actualBankAccounts = bankAccountService.getAll();

        // Assert
        assertThat(actualBankAccounts).usingRecursiveComparison()
                .ignoringFields("customersId", "issuedTransactions", "incomingTransactions")
                .isEqualTo(bankAccounts);
    }

    @Test
    @Order(2)
    void shouldFindBankAccount_whenBankAccountExists() {
        // Arrange
        long id = 1L;
        BankAccount bankAccount = new CheckingBankAccount();
        bankAccount.setId(id);
        bankAccount.setType(AccountType.CHECKING); // Replace CHECKING with your specific enum if different
        bankAccount.setBalance(Money.of(new BigDecimal("400.00")));
        bankAccount.setCustomersId(Set.of(1L));

        // Act
        BankAccount actualBankAccount = bankAccountService.findBankAccount(1L);

        // Assert
        assertThat(actualBankAccount)
                .usingRecursiveComparison()
                .ignoringFields("customersId", "issuedTransactions", "incomingTransactions")
                .isEqualTo(bankAccount);
    }

    @Test
    void shouldReturnNull_whenBankAccountDoesNotExist() {
        // Arrange
        long id = 99L;

        // Act
        try {
            bankAccountService.findBankAccount(id);
            fail("Bank account does not exist");
        } catch (BankAccountException exception) {
            // Assert
            String expectedMessage = "Bank account: searching failed - not found\n" + "Bank account id:" + id;
            assertThat(exception).hasMessage(expectedMessage);
        }
    }

    @Test
    void shouldAddTransactionToBankAccount_whenTransactionDoesNotExist() {
        // Arrange
        Instant timestamp = Instant.now();
        BankAccount bankAccount = new CheckingBankAccount();
        bankAccount.setId(99L);
        bankAccount.setType(AccountType.CHECKING);
        bankAccount.setBalance(Money.of(new BigDecimal("100")));
        bankAccount.setCustomersId(Set.of(99L));
        bankAccount.setIssuedTransactions(new HashSet<>());

        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setType(TransactionType.CREDIT);
        transaction.setEmitterAccountId(99L);
        transaction.setReceiverAccountId(77L);
        transaction.setAmount(new BigDecimal("100"));
        transaction.setCurrency("EUR");
        transaction.setStatus(TransactionStatus.ERROR);
        transaction.setDate(timestamp);
        transaction.setLabel("transaction test");

        // Assert
        BankAccount actualBankAccount = bankAccountService.putTransaction(transaction, bankAccount);
        assertThat(actualBankAccount.getIssuedTransactions()).hasSize(1);
        assertThat(actualBankAccount.getIssuedTransactions()).contains(transaction);
    }

    @Test
    void shouldUpdateTransactionToBankAccount_whenTransactionExists() {
        // Arrange
        Instant timestamp = Instant.now();
        BankAccount bankAccount = new CheckingBankAccount();
        bankAccount.setId(99L);
        bankAccount.setType(AccountType.CHECKING);
        bankAccount.setBalance(Money.of(new BigDecimal("100")));
        bankAccount.setCustomersId(Set.of(99L));
        bankAccount.setIssuedTransactions(new HashSet<>());

        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setType(TransactionType.CREDIT);
        transaction.setEmitterAccountId(99L);
        transaction.setReceiverAccountId(77L);
        transaction.setAmount(new BigDecimal("100"));
        transaction.setCurrency("EUR");
        transaction.setStatus(TransactionStatus.ERROR); // Assuming ERROR is an enum value
        transaction.setDate(timestamp); // Assuming timestamp is a defined Instant variable
        transaction.setLabel("transaction test");
        bankAccount.getIssuedTransactions().add(transaction);

        // Act
        BankAccount actualBankAccount = bankAccountService.putTransaction(transaction, bankAccount);

        // Assert
        assertThat(actualBankAccount.getIssuedTransactions()).hasSize(1);
        assertThat(actualBankAccount.getIssuedTransactions()).contains(transaction);
    }

    @Test
    void shouldUpdateBankAccount_whenHasValidBankAccount() {
        // Arrange
        long id = 8L;
        BankAccount bankAccount = new CheckingBankAccount();
        bankAccount.setId(id);
        bankAccount.setType(AccountType.CHECKING);
        bankAccount.setBalance(Money.of(new BigDecimal("0.00")));
        bankAccount.setCustomersId(Set.of(6L));

        Transaction transaction9 = new Transaction();
        transaction9.setId(9L);
        transaction9.setEmitterAccountId(8L);
        transaction9.setReceiverAccountId(7L);
        transaction9.setType(TransactionType.DEBIT);
        transaction9.setAmount(new BigDecimal("5000.00"));
        transaction9.setCurrency("EUR");
        transaction9.setStatus(TransactionStatus.UNPROCESSED);

        ZonedDateTime timestamp = ZonedDateTime.of(2024, 12, 6, 19, 0, 10, 0, ZoneOffset.ofHours(1));
        transaction9.setDate(timestamp.toInstant());

        transaction9.setLabel("transaction 9");
        transaction9.setMetadata(null);
        bankAccount.setIncomingTransactions(Set.of(transaction9));

        // Act
        BankAccount actualBankAccount = bankAccountService.updateBankAccount(bankAccount);

        // Assert
        BankAccount expectedBankAccount = bankAccountService.findBankAccount(id);
        assertThat(actualBankAccount)
                .usingRecursiveComparison()
                .isEqualTo(expectedBankAccount);
    }


    private static List<BankAccount> getAllBankAccounts() {
        BankAccount bankAccount1 = new CheckingBankAccount();
        bankAccount1.setId(1L);
        bankAccount1.setType(AccountType.CHECKING);
        bankAccount1.setBalance(Money.of(new BigDecimal("400.00")));
        bankAccount1.setCustomersId(Set.of(1L)); // Replace with appropriate customer IDs if available

        BankAccount bankAccount2 = new CheckingBankAccount();
        bankAccount2.setId(2L);
        bankAccount2.setType(AccountType.CHECKING);
        bankAccount2.setBalance(Money.of(new BigDecimal("1600.00")));
        bankAccount2.setCustomersId(Set.of(2L));

        BankAccount bankAccount3 = new SavingBankAccount();
        bankAccount3.setId(3L);
        bankAccount3.setType(AccountType.SAVING);
        bankAccount3.setBalance(Money.of(new BigDecimal("19200.00")));
        bankAccount3.setCustomersId(Set.of(3L));

        BankAccount bankAccount4 = new CheckingBankAccount();
        bankAccount4.setId(4L);
        bankAccount4.setType(AccountType.CHECKING);
        bankAccount4.setBalance(Money.of(new BigDecimal("500.00")));
        bankAccount4.setCustomersId(Set.of(4L));

        BankAccount bankAccount5 = new MMABankAccount(); // Assuming MMA corresponds to Money Market Account
        bankAccount5.setId(5L);
        bankAccount5.setType(AccountType.MMA);
        bankAccount5.setBalance(Money.of(new BigDecimal("65000.00")));
        bankAccount5.setCustomersId(Set.of(5L));

        BankAccount bankAccount6 = new SavingBankAccount();
        bankAccount6.setId(6L);
        bankAccount6.setType(AccountType.SAVING);
        bankAccount6.setBalance(Money.of(new BigDecimal("999.00")));
        bankAccount6.setCustomersId(Set.of(6L));

        BankAccount bankAccount7 = new CheckingBankAccount();
        bankAccount7.setId(7L);
        bankAccount7.setType(AccountType.CHECKING);
        bankAccount7.setBalance(Money.of(new BigDecimal("0.00")));
        bankAccount7.setCustomersId(Set.of(7L));

        BankAccount bankAccount8 = new SavingBankAccount();
        bankAccount8.setId(8L);
        bankAccount8.setType(AccountType.SAVING);
        bankAccount8.setBalance(Money.of(new BigDecimal("200000.00")));
        bankAccount8.setCustomersId(Set.of(8L));

        List<BankAccount> bankAccounts = List.of(bankAccount1, bankAccount2, bankAccount3, bankAccount4, bankAccount5, bankAccount6, bankAccount7, bankAccount8);
        return bankAccounts;
    }
}
