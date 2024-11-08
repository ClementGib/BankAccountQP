package com.cdx.bas.application.bank.transaction.type;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.bank.transaction.type.TransactionProcessorServicePort;
import com.cdx.bas.domain.bank.transaction.type.TransactionType;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static com.cdx.bas.domain.bank.account.type.AccountType.CHECKING;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
@WithTestResource(H2DatabaseTestResource.class)
class TransactionProcessorServiceImplTest {

    @InjectMock
    TransactionStatusServicePort transactionStatusService;

    @InjectMock
    BankAccountServicePort bankAccountService;

    @Inject
    TransactionProcessorServicePort transactionProcessingService;

    @Test
    void credit_shouldThrowNoSuchElementException_whenEmitterAccountIsNotFound() {
        // Arrange
        long emitterAccountId = 99L;
        long receiverAccountId = 77L;
        Instant instantDate = Instant.now();
        Transaction transaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        Map<String, String> metadata = Map.of("error", "Emitter bank account 99 is not found.");
        Transaction erroredTransaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(ERROR)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();

        when(bankAccountService.findBankAccount(emitterAccountId)).thenThrow(new NoSuchElementException("Emitter bank account 99 is not found."));
        when(transactionStatusService.saveStatus(eq(transaction), eq(ERROR), eq(metadata))).thenReturn(erroredTransaction);

        // Act
        try {
            transactionProcessingService.credit(transaction);
        } catch (NoSuchElementException exception) {
            // Assert
            assertThat(exception.getMessage()).isEqualTo("Transaction does not have emitter bank account entity.");
            verify(bankAccountService).findBankAccount(emitterAccountId);
            verifyNoMoreInteractions(bankAccountService, transactionStatusService);
        }
    }

    @Test
    void credit_shouldThrowNoSuchElementException_whenReceiverAccountIsNotFound() {
        // Arrange
        long emitterAccountId = 99L;
        long receiverAccountId = 77L;
        Money amountOfMoney = Money.of(new BigDecimal("0"));
        Instant instantDate = Instant.now();
        Transaction transaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        Map<String, String> metadata = Map.of("error", "Receiver bank account 77 is not found.");
        Transaction erroredTransaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(ERROR)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        BankAccount emitterBankAccount = CheckingBankAccount.builder()
                .id(emitterAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .issuedTransactions(Set.of(transaction))
                .build();

        when(bankAccountService.findBankAccount(emitterAccountId)).thenReturn(emitterBankAccount);
        when(bankAccountService.findBankAccount(receiverAccountId)).thenThrow(new NoSuchElementException("Receiver bank account 77 is not found."));
        when(transactionStatusService.saveStatus(eq(transaction), eq(ERROR), eq(metadata))).thenReturn(erroredTransaction);

        // Act
        try {
            transactionProcessingService.credit(transaction);
        } catch (NoSuchElementException exception) {
            // Assert
            assertThat(exception.getMessage()).isEqualTo("Transaction does not have emitter bank account entity.");
            verify(bankAccountService).findBankAccount(emitterAccountId);
            verify(bankAccountService).findBankAccount(receiverAccountId);
            verifyNoMoreInteractions(bankAccountService, transactionStatusService);
        }
    }

    @Test
    void credit_shouldReturnCompletedTransaction_whenAccountsAreFoundAndCreditTransactionIsValid() {
        // Arrange
        long emitterAccountId = 2L;
        long receiverAccountId = 1L;
        Money amountOfMoney = Mockito.mock(Money.class);
        Instant instantDate = Instant.now();
        Transaction transaction = Transaction.builder()
                .id(1L)
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .currency("EUR")
                .label("transaction test")
                .type(TransactionType.CREDIT)
                .build();
        Transaction outstandingTransaction = Transaction.builder()
                .id(1L)
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .currency("EUR")
                .label("transaction test")
                .type(TransactionType.CREDIT)
                .build();
        BankAccount emitterBankAccount = CheckingBankAccount.builder()
                .id(emitterAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .issuedTransactions(Set.of(transaction))
                .build();
        BankAccount receiverBankAccount = CheckingBankAccount.builder()
                .id(receiverAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .build();

        Map<String, String> metadataAfter = Map.of("emitter_amount_before", "1000",
                "receiver_amount_before", "0",
                "emitter_amount_after", "0",
                "receiver_amount_after", "1000");
        Transaction completedTransaction = Transaction.builder()
                .id(1L)
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(COMPLETED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        BankAccount updatedEmitterBankAccount = CheckingBankAccount.builder()
                .id(emitterAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .issuedTransactions(Set.of(completedTransaction))
                .build();

        when(bankAccountService.findBankAccount(emitterAccountId)).thenReturn(emitterBankAccount);
        when(bankAccountService.findBankAccount(receiverAccountId)).thenReturn(receiverBankAccount);
        when(transactionStatusService.setAsOutstanding(transaction)).thenReturn(outstandingTransaction);
        when(transactionStatusService.setStatus(transaction, COMPLETED, metadataAfter)).thenReturn(completedTransaction);
        when(amountOfMoney.getAmount())
                .thenReturn(new BigDecimal("1000"))
                .thenReturn(new BigDecimal("0"))
                .thenReturn(new BigDecimal("0"))
                .thenReturn(new BigDecimal("1000"));
        when(transactionStatusService.saveStatus(outstandingTransaction, COMPLETED, metadataAfter)).thenReturn(completedTransaction);
        when(bankAccountService.putTransaction(completedTransaction, emitterBankAccount)).thenReturn(updatedEmitterBankAccount);

        // Act
        Transaction returnedTransaction = transactionProcessingService.credit(transaction);
        receiverBankAccount.getBalance().plus(emitterBankAccount.getBalance());
        emitterBankAccount.getBalance().minus(emitterBankAccount.getBalance());

        // Assert
        assertThat(returnedTransaction).usingRecursiveComparison()
                .isEqualTo(Transaction.builder()
                        .id(1L)
                        .emitterAccountId(emitterAccountId)
                        .receiverAccountId(receiverAccountId)
                        .amount(new BigDecimal("1000"))
                        .status(COMPLETED)
                        .date(instantDate)
                        .metadata(new HashMap<>())
                        .build());
    }

    @Test
    void credit_shouldAddMoneyToTheReceiverAccount_whenAccountsAreFound_fromCreditTransaction() {
        // Arrange
        long emitterAccountId = 2L;
        long receiverAccountId = 1L;
        Money amountOfMoney = Mockito.mock(Money.class);
        Instant instantDate = Instant.now();
        Transaction oldTransaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        BankAccount emitterBankAccount = CheckingBankAccount.builder()
                .id(emitterAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .issuedTransactions(Set.of(oldTransaction))
                .build();
        BankAccount receiverBankAccount = CheckingBankAccount.builder()
                .id(receiverAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .build();

        Transaction transaction = Transaction.builder()
                .id(1L)
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .currency("EUR")
                .label("transaction test")
                .type(TransactionType.CREDIT)
                .build();
        Transaction outstandingTransaction = Transaction.builder()
                .id(1L)
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(OUTSTANDING)
                .date(instantDate)
                .metadata(new HashMap<>())
                .currency("EUR")
                .label("transaction test")
                .type(TransactionType.CREDIT)
                .build();

        Map<String, String> metadataAfter = Map.of("emitter_amount_before", "1000",
                "receiver_amount_before", "0",
                "emitter_amount_after", "0",
                "receiver_amount_after", "1000");
        Transaction completedTransaction = Transaction.builder()
                .id(1L)
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(COMPLETED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        BankAccount updatedEmitterBankAccount = CheckingBankAccount.builder()
                .id(emitterAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .issuedTransactions(Set.of(oldTransaction))
                .build();

        when(bankAccountService.findBankAccount(emitterAccountId)).thenReturn(emitterBankAccount);
        when(bankAccountService.findBankAccount(receiverAccountId)).thenReturn(receiverBankAccount);
        when(transactionStatusService.setAsOutstanding(transaction)).thenReturn(outstandingTransaction);
        when(amountOfMoney.getAmount())
                .thenReturn(new BigDecimal("1000"))
                .thenReturn(new BigDecimal("0"))
                .thenReturn(new BigDecimal("0"))
                .thenReturn(new BigDecimal("1000"));
        when(transactionStatusService.saveStatus(outstandingTransaction, COMPLETED, metadataAfter)).thenReturn(completedTransaction);
        when(bankAccountService.putTransaction(completedTransaction, emitterBankAccount)).thenReturn(updatedEmitterBankAccount);

        // Act
        transactionProcessingService.credit(transaction);

        // Assert
        verify(bankAccountService).findBankAccount(emitterAccountId);
        verify(bankAccountService).findBankAccount(receiverAccountId);
        verify(transactionStatusService).setAsOutstanding(transaction);
        verify(bankAccountService).creditAmountToAccounts(outstandingTransaction, emitterBankAccount, receiverBankAccount);
        verify(transactionStatusService).setStatus(transaction, COMPLETED, metadataAfter);
        verify(bankAccountService).updateBankAccount(updatedEmitterBankAccount);
        verify(bankAccountService).updateBankAccount(receiverBankAccount);
        verifyNoMoreInteractions(bankAccountService, transactionStatusService);
    }

    @Test
    void credit_shouldReturnRefusedTransaction_whenTransferThrowTransactionExceptionException() {
        // Arrange
        long emitterAccountId = 1L;
        long receiverAccountId = 2L;
        Money amountOfMoney = Mockito.mock(Money.class);
        Instant instantDate = Instant.now();
        Transaction oldTransaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("-1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        BankAccount emitterBankAccount = CheckingBankAccount.builder()
                .id(emitterAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .issuedTransactions(Set.of(oldTransaction))
                .build();
        BankAccount receiverBankAccount = CheckingBankAccount.builder()
                .id(receiverAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .build();

        Transaction transaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("-1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .currency("EUR")
                .label("transaction test")
                .type(TransactionType.CREDIT)
                .build();
        Transaction outstandingTransaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("-1000"))
                .status(OUTSTANDING)
                .date(instantDate)
                .metadata(new HashMap<>())
                .currency("EUR")
                .label("transaction test")
                .type(TransactionType.CREDIT)
                .build();

        when(bankAccountService.findBankAccount(emitterAccountId)).thenReturn(emitterBankAccount);
        when(bankAccountService.findBankAccount(receiverAccountId)).thenReturn(receiverBankAccount);
        when(transactionStatusService.setAsOutstanding(transaction)).thenReturn(outstandingTransaction);
        when(amountOfMoney.getAmount())
                .thenReturn(new BigDecimal("1000"))
                .thenReturn(new BigDecimal("0"));
        doThrow(new TransactionException("Credit transaction 1 should have positive value, actual value: -1000"))
                .when(bankAccountService).creditAmountToAccounts(outstandingTransaction, emitterBankAccount, receiverBankAccount);

        // Act
        try {
            transactionProcessingService.credit(transaction);
        } catch (TransactionException transactionException) {
            // Assert
            verify(bankAccountService).findBankAccount(emitterAccountId);
            verify(bankAccountService).findBankAccount(receiverAccountId);
            verify(transactionStatusService).setAsOutstanding(transaction);
            verify(bankAccountService).creditAmountToAccounts(outstandingTransaction, emitterBankAccount, receiverBankAccount);
            verifyNoMoreInteractions(bankAccountService, transactionStatusService);
        }
    }

    @Test
    void credit_shouldReturnErroredTransaction_whenAddTransactionThrowsBankAccountException() {
        // Arrange
        long emitterAccountId = 99L;
        long receiverAccountId = 101L;
        Money amountOfMoney = Mockito.mock(Money.class);
        Instant instantDate = Instant.now();
        Transaction oldTransaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        BankAccount emitterBankAccount = CheckingBankAccount.builder()
                .id(emitterAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .issuedTransactions(Set.of(oldTransaction))
                .build();
        BankAccount receiverBankAccount = CheckingBankAccount.builder()
                .id(receiverAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .build();

        Transaction transaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .currency("EUR")
                .label("transaction test")
                .type(TransactionType.CREDIT)
                .build();
        Transaction outstandingTransaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(OUTSTANDING)
                .date(instantDate)
                .metadata(new HashMap<>())
                .currency("EUR")
                .label("transaction test")
                .type(TransactionType.CREDIT)
                .build();

        Map<String, String> metadataDuringProcess = Map.of("emitter_amount_before", "0",
                "receiver_amount_before", "0",
                "emitter_amount_after", "-1000",
                "receiver_amount_after", "1000");
        Transaction completedTransaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(COMPLETED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        BankAccount updatedEmitterBankAccount = CheckingBankAccount.builder()
                .id(emitterAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(Set.of(99L))
                .issuedTransactions(Set.of(completedTransaction))
                .build();
        Map<String, String> metadataAfterError = Map.of("error", "Amount of credit should not be negative");

        when(bankAccountService.findBankAccount(emitterAccountId)).thenReturn(emitterBankAccount);
        when(bankAccountService.findBankAccount(receiverAccountId)).thenReturn(receiverBankAccount);
        when(transactionStatusService.setAsOutstanding(transaction)).thenReturn(outstandingTransaction);
        when(amountOfMoney.getAmount())
                .thenReturn(new BigDecimal("0"))
                .thenReturn(new BigDecimal("0"))
                .thenReturn(new BigDecimal("-1000"))
                .thenReturn(new BigDecimal("1000"));
        when(transactionStatusService.saveStatus(outstandingTransaction, COMPLETED, metadataDuringProcess)).thenReturn(completedTransaction);
        when(bankAccountService.putTransaction(completedTransaction, emitterBankAccount)).thenReturn(updatedEmitterBankAccount);
        doThrow(new BankAccountException("Amount of credit should not be negative")).when(bankAccountService).updateBankAccount(updatedEmitterBankAccount);

        // Act
        try {
            transactionProcessingService.credit(transaction);
        } catch (NoSuchElementException exception) {
            // Assert
            verify(bankAccountService).findBankAccount(emitterAccountId);
            verify(bankAccountService).findBankAccount(receiverAccountId);
            verify(transactionStatusService).setAsOutstanding(transaction);
            verify(bankAccountService).creditAmountToAccounts(outstandingTransaction, emitterBankAccount, receiverBankAccount);
            verify(bankAccountService).updateBankAccount(updatedEmitterBankAccount);
            verify(transactionStatusService).setStatus(transaction, COMPLETED, metadataDuringProcess);
            verifyNoMoreInteractions(bankAccountService, transactionStatusService);
        }
    }
}