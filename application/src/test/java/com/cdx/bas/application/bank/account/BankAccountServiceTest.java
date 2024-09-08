package com.cdx.bas.application.bank.account;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.BankAccountPersistencePort;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.account.validation.BankAccountValidator;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static com.cdx.bas.domain.bank.account.type.AccountType.CHECKING;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.ERROR;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static com.cdx.bas.domain.bank.transaction.type.TransactionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
class BankAccountServiceTest {

    @InjectMock
    BankAccountPersistencePort bankAccountRepository;

    @InjectMock
    BankAccountValidator bankAccountValidator;

    @InjectMock
    TransactionServicePort transactionService;

    @Inject
    BankAccountServicePort bankAccountService;

    @Test
    void shouldGetAllBankAccounts_whenRepositoryFoundBankAccounts() {
        // Arrange
        BankAccount bankAccount1 = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("100")))
                .customersId(Set.of(99L))
                .build();
        BankAccount bankAccount2 = CheckingBankAccount.builder()
                .id(100L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("10")))
                .customersId(Set.of(100L))
                .build();

        BankAccount bankAccount3 = CheckingBankAccount.builder()
                .id(101L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("20")))
                .customersId(Set.of(101L))
                .build();
        List<BankAccount> bankAccounts = List.of(bankAccount1, bankAccount2, bankAccount3);
        when(bankAccountRepository.getAll()).thenReturn(bankAccounts);

        // Act
        List<BankAccount> actualBankAccounts = bankAccountService.getAll();

        // Assert
        assertThat(actualBankAccounts).containsExactlyInAnyOrderElementsOf(bankAccounts);
    }

    @Test
    void shouldFindBankAccount_whenBankAccountExists() {
        // Arrange
        BankAccount bankAccount = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("100")))
                .customersId(Set.of(99L))
                .build();

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));

        // Act
        BankAccount actualBankAccount = bankAccountService.findBankAccount(1L);

        // Assert
        assertThat(actualBankAccount).isEqualTo(bankAccount);
        verify(bankAccountRepository).findById(1L);
        verifyNoMoreInteractions(bankAccountRepository);
        verifyNoInteractions(transactionService);
    }

    @Test
    void shouldReturnNull_whenBankAccountDoesNotExist() {
        // Arrange
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        try {
            bankAccountService.findBankAccount(1L);
            fail("Bank acocunt does not exist");
        } catch (BankAccountException exception) {
            // Assert
            assertThat(exception).hasMessage("Missing bank account with id: 1");
            verify(bankAccountRepository).findById(1L);
            verifyNoMoreInteractions(bankAccountRepository, bankAccountValidator);
            verifyNoInteractions(transactionService);
        }
    }

    @Test
    void shouldAddTransactionToBankAccount_whenTransactionDoesNotExist() {
        // Arrange
        Instant timestamp = Instant.now();
        BankAccount bankAccount = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("100")))
                .customersId(Set.of(99L))
                .issuedTransactions(new HashSet<>())
                .build();

        // Act
        Transaction transaction = Transaction.builder()
                .id(10L)
                .type(CREDIT)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("100"))
                .currency("EUR")
                .status(ERROR)
                .date(timestamp)
                .label("transaction test")
                .build();

        // Assert
        BankAccount actualBankAccount = bankAccountService.putTransaction(transaction, bankAccount);
        assertThat(actualBankAccount.getIssuedTransactions()).hasSize(1);
        assertThat(actualBankAccount.getIssuedTransactions()).contains(transaction);

        verifyNoInteractions(transactionService, bankAccountValidator, bankAccountRepository);
    }

    @Test
    void shouldUpdateTransactionToBankAccount_whenTransactionExists() {
        // Arrange
        Instant timestamp = Instant.now();
        BankAccount bankAccount = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("100")))
                .customersId(Set.of(99L))
                .issuedTransactions(new HashSet<>())
                .build();
        Transaction transaction = Transaction.builder()
                .id(10L)
                .type(CREDIT)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("100"))
                .currency("EUR")
                .status(ERROR)
                .date(timestamp)
                .label("transaction test")
                .build();
        bankAccount.getIssuedTransactions().add(transaction);


        // Act
        BankAccount actualBankAccount = bankAccountService.putTransaction(transaction, bankAccount);

        // Assert
        assertThat(actualBankAccount.getIssuedTransactions()).hasSize(1);
        assertThat(actualBankAccount.getIssuedTransactions()).contains(transaction);
        verifyNoMoreInteractions(transactionService);
        verifyNoInteractions(bankAccountValidator, bankAccountRepository);
    }

    @Test
    void shouldUpdateBankAccount_whenHasValidBankAccount() {
        // Arrange
        BankAccount bankAccount = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("100")))
                .customersId(Set.of(99L))
                .build();

        // Act
        BankAccount actualBankAccount = bankAccountService.updateBankAccount(bankAccount);

        // Assert
        assertThat(actualBankAccount).isNull();
        verify(bankAccountValidator).validateBankAccount(bankAccount);
        verify(bankAccountRepository).update(bankAccount);
        verifyNoMoreInteractions(bankAccountValidator, bankAccountRepository);
        verifyNoInteractions(transactionService);
    }

    @Test
    void shouldCreditBankAccountWithAmount_whenHasValidCreditTransaction() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(10L)
                .type(CREDIT)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("100"))
                .currency("EUR")
                .status(ERROR)
                .date(Instant.now())
                .label("transaction test")
                .build();
        BankAccount bankAccountEmitter = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("100")))
                .customersId(Set.of(99L))
                .build();
        BankAccount bankAccountReceiver = CheckingBankAccount.builder()
                .id(100L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("0")))
                .customersId(Set.of(99L))
                .build();

        // Act
        bankAccountService.creditAmountToAccounts(transaction, bankAccountEmitter, bankAccountReceiver);

        // Assert
        assertThat(bankAccountEmitter.getBalance().getAmount()).isEqualTo(new BigDecimal("0.0"));
        assertThat(bankAccountReceiver.getBalance().getAmount()).isEqualTo(new BigDecimal("100.0"));
        verifyNoInteractions(transactionService, bankAccountValidator, bankAccountRepository);
    }

    @Test
    void shouldThrowTransactionException_whenCreditTransactionHasNegativeAmount() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(10L)
                .type(CREDIT)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("-100"))
                .currency("EUR")
                .status(ERROR)
                .date(Instant.now())
                .label("transaction test")
                .build();
        BankAccount bankAccountEmitter = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("100")))
                .customersId(Set.of(99L))
                .build();
        BankAccount bankAccountReceiver = CheckingBankAccount.builder()
                .id(100L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("0")))
                .customersId(Set.of(99L))
                .build();

        // Act
        try {
            bankAccountService.creditAmountToAccounts(transaction, bankAccountEmitter, bankAccountReceiver);
        } catch (TransactionException exception) {
            // Assert
            assertThat(exception.getMessage()).isEqualTo("Credit: 10 should have positive value, actual value: -100.0");
            verifyNoInteractions(transactionService, bankAccountValidator, bankAccountRepository);
        }
    }

    @Test
    void shouldDepositAmountToBankAccount_whenHasValidDepositTransaction() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(10L)
                .type(DEPOSIT)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("100"))
                .currency("EUR")
                .status(UNPROCESSED)
                .date(Instant.now())
                .label("transaction test")
                .build();
        BankAccount bankAccountEmitter = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("0")))
                .customersId(Set.of(99L))
                .build();

        // Act
        bankAccountService.depositAmountToAccount(transaction, bankAccountEmitter);

        // Assert
        assertThat(bankAccountEmitter.getBalance().getAmount()).isEqualTo(new BigDecimal("100.0"));
        verifyNoInteractions(transactionService, bankAccountValidator, bankAccountRepository);
    }

    @Test
    void shouldThrowTransactionException_whenDepositTransactionHasNegativeAmount() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(10L)
                .type(DEPOSIT)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("-100"))
                .currency("EUR")
                .status(UNPROCESSED)
                .date(Instant.now())
                .label("transaction test")
                .build();
        BankAccount bankAccountEmitter = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("100")))
                .customersId(Set.of(99L))
                .build();

        // Act
        try {
            bankAccountService.depositAmountToAccount(transaction, bankAccountEmitter);
        } catch (TransactionException exception) {
            // Assert
            assertThat(exception.getMessage()).isEqualTo("Deposit: 10 should have positive value, actual value: -100.0");
            verifyNoInteractions(transactionService, bankAccountValidator, bankAccountRepository);
        }
    }

    @Test
    void shouldWithdrawAmountToBankAccount_whenHasValidWithdrawTransaction() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(10L)
                .type(WITHDRAW)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("100"))
                .currency("EUR")
                .status(UNPROCESSED)
                .date(Instant.now())
                .label("transaction test")
                .build();
        BankAccount bankAccountEmitter = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("100")))
                .customersId(Set.of(99L))
                .build();

        // Act
        bankAccountService.withdrawAmountToAccount(transaction, bankAccountEmitter);

        // Assert
        assertThat(bankAccountEmitter.getBalance().getAmount()).isEqualTo(new BigDecimal("0.0"));
        verifyNoInteractions(transactionService, bankAccountValidator, bankAccountRepository);
    }

    @Test
    void shouldThrowTransactionException_whenWithdrawTransactionHasNegativeAmount() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(10L)
                .type(WITHDRAW)
                .emitterAccountId(99L)
                .receiverAccountId(77L)
                .amount(new BigDecimal("-100"))
                .currency("EUR")
                .status(UNPROCESSED)
                .date(Instant.now())
                .label("transaction test")
                .build();
        BankAccount bankAccountEmitter = CheckingBankAccount.builder()
                .id(99L)
                .type(CHECKING)
                .balance(Money.of(new BigDecimal("100")))
                .customersId(Set.of(99L))
                .build();

        // Act
        try {
            bankAccountService.withdrawAmountToAccount(transaction, bankAccountEmitter);
        } catch (TransactionException exception) {
            // Assert
            assertThat(exception.getMessage()).isEqualTo("Withdraw: 10 should have positive value, actual value: -100.0");
            verifyNoInteractions(transactionService, bankAccountValidator, bankAccountRepository);
        }
    }
}
