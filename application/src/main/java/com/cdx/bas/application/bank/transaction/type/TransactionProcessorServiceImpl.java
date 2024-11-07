package com.cdx.bas.application.bank.transaction.type;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.bank.transaction.type.TransactionProcessorServicePort;
import com.cdx.bas.domain.bank.transaction.validation.validator.TransactionValidator;
import com.cdx.bas.domain.message.MessageFormatter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.*;
import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.metadata.MetadataFieldNames.*;


@ApplicationScoped
public class TransactionProcessorServiceImpl implements TransactionProcessorServicePort {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessorServiceImpl.class);

    private final TransactionValidator transactionValidator;
    private final TransactionStatusServicePort transactionStatusService;
    private final TransactionServicePort transactionService;
    private final BankAccountServicePort bankAccountService;

    @Inject
    public TransactionProcessorServiceImpl(TransactionValidator transactionValidator,
                                                TransactionStatusServicePort transactionStatusService,
                                                TransactionServicePort transactionService,
                                                BankAccountServicePort bankAccountService) {
        this.transactionValidator = transactionValidator;
        this.transactionStatusService = transactionStatusService;
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;
    }

    @Override
    @Transactional
    public Transaction credit(Transaction transaction) {
        Map<String, String> metadata = new HashMap<>();
        TransactionStatus transactionStatus = COMPLETED;
        try {
            BankAccount emitterBankAccount = bankAccountService.findBankAccount(transaction.getEmitterAccountId());
            BankAccount receiverBankAccount = bankAccountService.findBankAccount(transaction.getReceiverAccountId());
            Transaction currentTransaction = transactionStatusService.setAsOutstanding(transaction);

            metadata = new HashMap<>();
            metadata.put(EMITTER_AMOUNT_BEFORE_KEY, emitterBankAccount.getBalance().getAmount().toString());
            metadata.put(RECEIVER_AMOUNT_BEFORE_KEY, receiverBankAccount.getBalance().getAmount().toString());
            bankAccountService.creditAmountToAccounts(currentTransaction, emitterBankAccount, receiverBankAccount);
            metadata.put(EMITTER_AMOUNT_AFTER_KEY, emitterBankAccount.getBalance().getAmount().toString());
            metadata.put(RECEIVER_AMOUNT_AFTER_KEY, receiverBankAccount.getBalance().getAmount().toString());

            Transaction completedTransaction = transactionStatusService.setStatus(currentTransaction, COMPLETED, metadata);
            bankAccountService.updateBankAccount(emitterBankAccount);
            bankAccountService.updateBankAccount(receiverBankAccount);

            logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, CREDIT_ACTION, COMPLETED_STATUS,
                    List.of(CREDIT_DETAIL + currentTransaction.getAmount(),
                            EMITTER_BANK_ACCOUNT_DETAIL + currentTransaction.getEmitterAccountId(),
                            RECEIVER_BANK_ACCOUNT_DETAIL + currentTransaction.getReceiverAccountId())));
            return completedTransaction;
        } catch (NoSuchElementException exception) {
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = ERROR;
            throw new TransactionException(MessageFormatter.format(TRANSACTION_CONTEXT, CREDIT_ACTION, FAILED_STATUS,
                    Optional.of(NOT_FOUND_CAUSE),
                    List.of(TRANSACTION_ID_DETAIL + transaction.getId(), ERROR_DETAIL + exception.getMessage())));
        } catch (TransactionException exception) {
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new TransactionException(MessageFormatter.format(TRANSACTION_CONTEXT, CREDIT_ACTION, REFUSED_STATUS,
                    Optional.of(TRANSACTION_ERROR_CAUSE),
                    List.of(TRANSACTION_ID_DETAIL + transaction.getId(),
                            ERROR_DETAIL + exception.getMessage())));
        } catch (BankAccountException exception) {
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new TransactionException(MessageFormatter.format(TRANSACTION_CONTEXT, CREDIT_ACTION, REFUSED_STATUS,
                    Optional.of(BANK_ACCOUNT_ERROR_CAUSE),
                    List.of(TRANSACTION_ID_DETAIL + transaction.getId(),
                            ERROR_DETAIL + exception.getMessage())));
        } finally {
            transaction.setStatus(transactionStatus);
            transactionService.update(transaction, metadata);
        }
    }

    @Override
    public Transaction debit(Transaction currentTransaction) {
        //TODO feature#45
        currentTransaction.setStatus(REFUSED);
        transactionService.update(currentTransaction, new HashMap<>());
        return currentTransaction;
    }

    @Override
    @Transactional
    public Transaction deposit(Transaction currentTransaction) {
        Map<String, String> metadata = new HashMap<>();
        TransactionStatus transactionStatus = COMPLETED;
        try {
            transactionValidator.validateCashTransaction(currentTransaction);
            BankAccount emitterBankAccount = bankAccountService.findBankAccount(currentTransaction.getEmitterAccountId());

            metadata = new HashMap<>();
            metadata.put(EMITTER_AMOUNT_BEFORE_KEY, emitterBankAccount.getBalance().getAmount().toString());
            bankAccountService.depositAmountToAccount(currentTransaction, emitterBankAccount);
            metadata.put(EMITTER_AMOUNT_AFTER_KEY, emitterBankAccount.getBalance().getAmount().toString());
            Transaction completedTransaction = transactionStatusService.setStatus(currentTransaction, COMPLETED, metadata);
            bankAccountService.updateBankAccount(emitterBankAccount);

            logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, DEPOSIT_ACTION, COMPLETED_STATUS,
                    List.of(TRANSACTION_ID_DETAIL + currentTransaction.getId(),
                            CREDIT_DETAIL + currentTransaction.getAmount(),
                            EMITTER_BANK_ACCOUNT_DETAIL + currentTransaction.getEmitterAccountId(),
                            RECEIVER_BANK_ACCOUNT_DETAIL + currentTransaction.getReceiverAccountId())));
            return completedTransaction;
        } catch (TransactionException exception) {
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new TransactionException(MessageFormatter.format(TRANSACTION_CONTEXT, DEPOSIT_ACTION, REFUSED_STATUS,
                    Optional.of(TRANSACTION_ERROR_CAUSE),
                    List.of(TRANSACTION_ID_DETAIL + currentTransaction.getId(),
                            ERROR_DETAIL + exception.getMessage())));
        } catch (BankAccountException exception) {
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new TransactionException(MessageFormatter.format(TRANSACTION_CONTEXT, DEPOSIT_ACTION, REFUSED_STATUS,
                    Optional.of(BANK_ACCOUNT_ERROR_CAUSE),
                    List.of(TRANSACTION_ID_DETAIL + currentTransaction.getId(),
                            ERROR_DETAIL + exception.getMessage())));
        } finally {
            currentTransaction.setStatus(transactionStatus);
            transactionService.create(currentTransaction, metadata);
        }
    }

    @Override
    @Transactional
    public Transaction withdraw(Transaction currentTransaction) {
        Map<String, String> metadata = new HashMap<>();
        TransactionStatus transactionStatus = COMPLETED;
        try {
            transactionValidator.validateCashTransaction(currentTransaction);
            BankAccount emitterBankAccount = bankAccountService.findBankAccount(currentTransaction.getEmitterAccountId());
            metadata = new HashMap<>();
            metadata.put(EMITTER_AMOUNT_BEFORE_KEY, emitterBankAccount.getBalance().getAmount().toString());
            bankAccountService.withdrawAmountToAccount(currentTransaction, emitterBankAccount);
            metadata.put(EMITTER_AMOUNT_AFTER_KEY, emitterBankAccount.getBalance().getAmount().toString());
            Transaction completedTransaction = transactionStatusService.setStatus(currentTransaction, COMPLETED, metadata);
            bankAccountService.updateBankAccount(emitterBankAccount);

            logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, WITHDRAW_ACTION, COMPLETED_STATUS,
                    List.of(TRANSACTION_ID_DETAIL + currentTransaction.getId(),
                            CREDIT_DETAIL + currentTransaction.getAmount(),
                            EMITTER_BANK_ACCOUNT_DETAIL + currentTransaction.getEmitterAccountId(),
                            RECEIVER_BANK_ACCOUNT_DETAIL + currentTransaction.getReceiverAccountId())));
            return completedTransaction;
        } catch (TransactionException exception) {
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new TransactionException(MessageFormatter.format(TRANSACTION_CONTEXT, WITHDRAW_ACTION, REFUSED_STATUS,
                    Optional.of(TRANSACTION_ERROR_CAUSE),
                    List.of(TRANSACTION_ID_DETAIL + currentTransaction.getId(),
                            ERROR_DETAIL + exception.getMessage())));
        } catch (BankAccountException exception) {
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new TransactionException(MessageFormatter.format(TRANSACTION_CONTEXT, WITHDRAW_ACTION, REFUSED_STATUS,
                    Optional.of(BANK_ACCOUNT_ERROR_CAUSE),
                    List.of(TRANSACTION_ID_DETAIL + currentTransaction.getId(),
                            ERROR_DETAIL + exception.getMessage())));
        } finally {
            currentTransaction.setStatus(transactionStatus);
            transactionService.create(currentTransaction, metadata);
        }
    }
}


