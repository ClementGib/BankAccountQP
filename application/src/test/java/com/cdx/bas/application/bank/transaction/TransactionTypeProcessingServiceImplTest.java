package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.account.checking.CheckingBankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.bank.transaction.type.TransactionTypeProcessingServicePort;
import com.cdx.bas.domain.money.Money;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static com.cdx.bas.domain.bank.account.type.AccountType.CHECKING;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
class TransactionTypeProcessingServiceImplTest {

    @InjectMock
    TransactionStatusServicePort transactionStatusService;

    @InjectMock
    BankAccountServicePort bankAccountService;

    @Inject
    TransactionTypeProcessingServicePort transactionProcessingService;

    @Test
    public void credit_shouldThrowNoSuchElementException_whenEmitterAccountIsNotFound() {
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
        when(transactionStatusService.setStatus(eq(transaction), eq(ERROR), eq(metadata))).thenReturn(erroredTransaction);

        // Act
        Transaction returnedTransaction =  transactionProcessingService.credit(transaction);

        // Assert
        assertThat(returnedTransaction).usingRecursiveComparison()
                .isEqualTo(Transaction.builder()
                        .emitterAccountId(emitterAccountId)
                        .receiverAccountId(receiverAccountId)
                        .amount(new BigDecimal("1000"))
                        .status(ERROR)
                        .date(instantDate)
                        .metadata(new HashMap<>())
                        .build());
        verify(bankAccountService).findBankAccount(eq(emitterAccountId));
        verify(transactionStatusService).setStatus(eq(transaction), eq(ERROR), eq(metadata));
        verifyNoMoreInteractions(bankAccountService, transactionStatusService);
    }

    @Test
    public void credit_shouldThrowNoSuchElementException_whenReceiverAccountIsNotFound() {
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
                .customersId(List.of(99L))
                .issuedTransactions(Set.of(transaction))
                .build();

        when(bankAccountService.findBankAccount(emitterAccountId)).thenReturn(emitterBankAccount);
        when(bankAccountService.findBankAccount(receiverAccountId)).thenThrow(new NoSuchElementException("Receiver bank account 77 is not found."));
        when(transactionStatusService.setStatus(eq(transaction), eq(ERROR), eq(metadata))).thenReturn(erroredTransaction);

        // Act
        Transaction returnedTransaction =  transactionProcessingService.credit(transaction);

        // Assert
        assertThat(returnedTransaction).usingRecursiveComparison()
                .isEqualTo(Transaction.builder()
                        .emitterAccountId(emitterAccountId)
                        .receiverAccountId(receiverAccountId)
                        .amount(new BigDecimal("1000"))
                        .status(ERROR)
                        .date(instantDate)
                        .metadata(new HashMap<>())
                        .build());
        verify(bankAccountService).findBankAccount(eq(emitterAccountId));
        verify(bankAccountService).findBankAccount(eq(receiverAccountId));
        verify(transactionStatusService).setStatus(eq(transaction), eq(ERROR), eq(metadata));
        verifyNoMoreInteractions(bankAccountService, transactionStatusService);
    }

    @Test
    public void credit_shouldReturnCompletedTransaction_whenAccountsAreFoundAndCreditTransactionIsValid() {
        // Arrange
        long emitterAccountId = 99L;
        long receiverAccountId = 77L;
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
                .build();
        Transaction outstandingTransaction = Transaction.builder()
                .id(1L)
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
                .customersId(List.of(99L))
                .issuedTransactions(Set.of(transaction))
                .build();
        BankAccount receiverBankAccount = CheckingBankAccount.builder()
                .id(receiverAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(List.of(99L))
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
                .customersId(List.of(99L))
                .issuedTransactions(Set.of(completedTransaction))
                .build();

        when(bankAccountService.findBankAccount(emitterAccountId)).thenReturn(emitterBankAccount);
        when(bankAccountService.findBankAccount(receiverAccountId)).thenReturn(receiverBankAccount);
        when(transactionStatusService.setAsOutstanding(transaction)).thenReturn(outstandingTransaction);
        when(amountOfMoney.getAmount())
                .thenReturn(new BigDecimal("1000"))
                .thenReturn(new BigDecimal("0"))
                .thenReturn(new BigDecimal("0"))
                .thenReturn(new BigDecimal("1000"));
        when(transactionStatusService.setStatus(outstandingTransaction, COMPLETED, metadataAfter)).thenReturn(completedTransaction);
        when(bankAccountService.addTransaction(completedTransaction, emitterBankAccount)).thenReturn(updatedEmitterBankAccount);

        // Act
        Transaction returnedTransaction =  transactionProcessingService.credit(transaction);
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
    public void credit_shouldAddMoneyToTheReceiverAccount_whenAccountsAreFound_fromCreditTransaction() {
        // Arrange
        long emitterAccountId = 99L;
        long receiverAccountId = 77L;
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
                .customersId(List.of(99L))
                .issuedTransactions(Set.of(oldTransaction))
                .build();
        BankAccount receiverBankAccount = CheckingBankAccount.builder()
                .id(receiverAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(List.of(99L))
                .build();

        Transaction transaction = Transaction.builder()
                .id(1L)
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        Transaction outstandingTransaction = Transaction.builder()
                .id(1L)
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(OUTSTANDING)
                .date(instantDate)
                .metadata(new HashMap<>())
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
                .customersId(List.of(99L))
                .issuedTransactions(Set.of(completedTransaction))
                .build();

        when(bankAccountService.findBankAccount(emitterAccountId)).thenReturn(emitterBankAccount);
        when(bankAccountService.findBankAccount(receiverAccountId)).thenReturn(receiverBankAccount);
        when(transactionStatusService.setAsOutstanding(transaction)).thenReturn(outstandingTransaction);
        when(amountOfMoney.getAmount())
                .thenReturn(new BigDecimal("1000"))
                .thenReturn(new BigDecimal("0"))
                .thenReturn(new BigDecimal("0"))
                .thenReturn(new BigDecimal("1000"));
        when(transactionStatusService.setStatus(outstandingTransaction, COMPLETED, metadataAfter)).thenReturn(completedTransaction);
        when(bankAccountService.addTransaction(completedTransaction, emitterBankAccount)).thenReturn(updatedEmitterBankAccount);

        // Act
        transactionProcessingService.credit(transaction);

        // Assert
        verify(bankAccountService).findBankAccount(eq(emitterAccountId));
        verify(bankAccountService).findBankAccount(eq(receiverAccountId));
        verify(transactionStatusService).setAsOutstanding(transaction);
        verify(bankAccountService).transferAmountBetweenAccounts(outstandingTransaction, emitterBankAccount, receiverBankAccount);
        verify(transactionStatusService).setStatus(eq(outstandingTransaction), eq(COMPLETED), eq(metadataAfter));
        verify(bankAccountService).addTransaction(completedTransaction, emitterBankAccount);
        verify(bankAccountService).updateBankAccount(updatedEmitterBankAccount);
        verify(bankAccountService).updateBankAccount(receiverBankAccount);
        verifyNoMoreInteractions(bankAccountService, transactionStatusService);
    }

    @Test
    public void credit_shouldReturnRefusedTransaction_whenTransferThrowTransactionExceptionException() {
        // Arrange
        long emitterAccountId = 99L;
        long receiverAccountId = 77L;
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
                .customersId(List.of(99L))
                .issuedTransactions(Set.of(oldTransaction))
                .build();
        BankAccount receiverBankAccount = CheckingBankAccount.builder()
                .id(receiverAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(List.of(99L))
                .build();

        Transaction transaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("-1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        Transaction outstandingTransaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("-1000"))
                .status(OUTSTANDING)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        Map<String, String> metadataAfter = Map.of("error", "Credit transaction 1 should have positive value, actual value: -1000");

        when(bankAccountService.findBankAccount(emitterAccountId)).thenReturn(emitterBankAccount);
        when(bankAccountService.findBankAccount(receiverAccountId)).thenReturn(receiverBankAccount);
        when(transactionStatusService.setAsOutstanding(transaction)).thenReturn(outstandingTransaction);
        when(amountOfMoney.getAmount())
                .thenReturn(new BigDecimal("1000"))
                .thenReturn(new BigDecimal("0"));
        doThrow(new TransactionException("Credit transaction 1 should have positive value, actual value: -1000"))
                .when(bankAccountService).transferAmountBetweenAccounts(outstandingTransaction, emitterBankAccount, receiverBankAccount);

        // Act
        transactionProcessingService.credit(transaction);

        // Assert
        verify(bankAccountService).findBankAccount(eq(emitterAccountId));
        verify(bankAccountService).findBankAccount(eq(receiverAccountId));
        verify(transactionStatusService).setAsOutstanding(transaction);
        verify(bankAccountService).transferAmountBetweenAccounts(outstandingTransaction, emitterBankAccount, receiverBankAccount);
        verify(transactionStatusService).setStatus(transaction, REFUSED, metadataAfter);
        verifyNoMoreInteractions(bankAccountService, transactionStatusService);
    }

    @Test
    public void credit_shouldReturnErroredTransaction_whenAddTransactionThrowsBankAccountException() {
        // Arrange
        long emitterAccountId = 99L;
        long receiverAccountId = 77L;
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
                .customersId(List.of(99L))
                .issuedTransactions(Set.of(oldTransaction))
                .build();
        BankAccount receiverBankAccount = CheckingBankAccount.builder()
                .id(receiverAccountId)
                .type(CHECKING)
                .balance(amountOfMoney)
                .customersId(List.of(99L))
                .build();

        Transaction transaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(UNPROCESSED)
                .date(instantDate)
                .metadata(new HashMap<>())
                .build();
        Transaction outstandingTransaction = Transaction.builder()
                .emitterAccountId(emitterAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(new BigDecimal("1000"))
                .status(OUTSTANDING)
                .date(instantDate)
                .metadata(new HashMap<>())
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
                .customersId(List.of(99L))
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
        when(transactionStatusService.setStatus(outstandingTransaction, COMPLETED, metadataDuringProcess)).thenReturn(completedTransaction);
        when(bankAccountService.addTransaction(completedTransaction, emitterBankAccount)).thenReturn(updatedEmitterBankAccount);
        doThrow(new BankAccountException("Amount of credit should not be negative")).when(bankAccountService).updateBankAccount(updatedEmitterBankAccount);

        // Act
        transactionProcessingService.credit(transaction);

        // Assert
        verify(bankAccountService).findBankAccount(eq(emitterAccountId));
        verify(bankAccountService).findBankAccount(eq(receiverAccountId));
        verify(transactionStatusService).setAsOutstanding(transaction);
        verify(bankAccountService).transferAmountBetweenAccounts(outstandingTransaction, emitterBankAccount, receiverBankAccount);
        verify(transactionStatusService).setStatus(outstandingTransaction, COMPLETED, metadataDuringProcess);
        verify(bankAccountService).addTransaction(completedTransaction, emitterBankAccount);
        verify(bankAccountService).updateBankAccount(updatedEmitterBankAccount);
        verify(transactionStatusService).setStatus(transaction, REFUSED, metadataAfterError);
        verifyNoMoreInteractions(bankAccountService, transactionStatusService);
    }
}