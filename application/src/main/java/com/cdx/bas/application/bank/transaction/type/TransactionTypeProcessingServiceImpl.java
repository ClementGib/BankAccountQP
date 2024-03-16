package com.cdx.bas.application.bank.transaction.type;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.bank.transaction.type.TransactionTypeProcessingServicePort;
import com.cdx.bas.domain.bank.transaction.validation.validator.TransactionValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.*;
import static com.cdx.bas.domain.metadata.MetadataConstant.*;
import static com.cdx.bas.domain.text.MessageConstants.*;

@ApplicationScoped
public class TransactionTypeProcessingServiceImpl implements TransactionTypeProcessingServicePort {

    private static final Logger logger = LoggerFactory.getLogger(TransactionTypeProcessingServiceImpl.class);

    private final TransactionValidator transactionValidator;
    private final TransactionStatusServicePort transactionStatusService;
    private final TransactionServicePort transactionService;
    private final BankAccountServicePort bankAccountService;

    @Inject
    public TransactionTypeProcessingServiceImpl(TransactionValidator transactionValidator,
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
            BankAccount emitterBankAccount = getEmitter(transaction);
            BankAccount receiverBankAccount = bankAccountService.findBankAccount(transaction.getReceiverAccountId());
            Transaction currentTransaction = transactionStatusService.setAsOutstanding(transaction);

            logger.info(DEBIT_OF_CONTENT + currentTransaction.getAmount()
                    + FROM_BANK_ACCOUNT_CONTENT + currentTransaction.getEmitterAccountId()
                    + TO_BANK_ACCOUNT_CONTENT + currentTransaction.getReceiverAccountId());

            metadata = new HashMap<>();
            metadata.put(EMITTER_AMOUNT_BEFORE_KEY, emitterBankAccount.getBalance().getAmount().toString());
            metadata.put(RECEIVER_AMOUNT_BEFORE_KEY, receiverBankAccount.getBalance().getAmount().toString());
            bankAccountService.creditAmountToAccounts(currentTransaction, emitterBankAccount, receiverBankAccount);
            metadata.put(EMITTER_AMOUNT_AFTER_KEY, emitterBankAccount.getBalance().getAmount().toString());
            metadata.put(RECEIVER_AMOUNT_AFTER_KEY, receiverBankAccount.getBalance().getAmount().toString());

            Transaction completedTransaction = transactionStatusService.setStatus(currentTransaction, COMPLETED, metadata);
            bankAccountService.updateBankAccount(emitterBankAccount);
            bankAccountService.updateBankAccount(receiverBankAccount);
            logger.info(BANK_ACCOUNT_START + emitterBankAccount.getId() + " credit transaction " + currentTransaction.getId() + " completed.");
            return completedTransaction;
        } catch (NoSuchElementException exception) {
            logger.error(TRANSACTION_START + transaction.getId() + " credit error for amount " + transaction.getAmount() + ": " + exception.getMessage());
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = ERROR;
            throw new TransactionException("Transaction error while credit transaction: " + exception.getMessage());
        } catch (TransactionException exception) {
            logger.error(TRANSACTION_START + transaction.getId() + " of " + transaction.getAmount() + " is invalid.");
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new TransactionException("Transaction error while credit transaction: " + exception.getMessage());
        } catch (BankAccountException exception) {
            logger.error(TRANSACTION_START + transaction.getId() + " credit refused for amount " + transaction.getAmount() + ": " + exception.getMessage());
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new BankAccountException("Bank account refused while credit transaction: " + exception.getMessage());
        } finally {
            transaction.setStatus(transactionStatus);
            transactionService.update(transaction, metadata);
        }
    }

    private BankAccount getEmitter(Transaction transaction) {
        logger.info(TRANSACTION_START + FOR_EMITTER + transaction.getEmitterAccountId() + PROCESSING_END);
        return bankAccountService.findBankAccount(transaction.getEmitterAccountId());
    }

    @Override
    public Transaction debit(Transaction currentTransaction) {
        transactionValidator.validateCashTransaction(currentTransaction);
        //TODO
        return null;
    }

    @Override
    @Transactional
    public Transaction deposit(Transaction currentTransaction) {
        Map<String, String> metadata = new HashMap<>();
        TransactionStatus transactionStatus = COMPLETED;
        try {
            transactionValidator.validateCashTransaction(currentTransaction);
            logger.info(DEPOSIT_OF_CONTENT + currentTransaction.getAmount() + VALIDATED_END);
            BankAccount emitterBankAccount = getEmitter(currentTransaction);
            logger.info(DEPOSIT_OF_CONTENT + currentTransaction.getAmount()
                    + FROM_BANK_ACCOUNT_CONTENT + currentTransaction.getEmitterAccountId());

            metadata = new HashMap<>();
            metadata.put(EMITTER_AMOUNT_BEFORE_KEY, emitterBankAccount.getBalance().getAmount().toString());
            bankAccountService.depositAmountToAccount(currentTransaction, emitterBankAccount);
            metadata.put(EMITTER_AMOUNT_AFTER_KEY, emitterBankAccount.getBalance().getAmount().toString());
            Transaction completedTransaction = transactionStatusService.setStatus(currentTransaction, COMPLETED, metadata);
            bankAccountService.updateBankAccount(emitterBankAccount);
            return completedTransaction;
        } catch (TransactionException exception) {
            logger.error(TRANSACTION_START + currentTransaction.getId() + " of " + currentTransaction.getAmount() + " is invalid.");
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new TransactionException("Transaction error while deposit transaction: " + exception.getMessage());
        } catch (BankAccountException exception) {
            logger.error(TRANSACTION_START + currentTransaction.getId() + " deposit refused for amount " + currentTransaction.getAmount() + ": " + exception.getMessage());
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new BankAccountException("Bank account refused while deposit transaction: " + exception.getMessage());
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
            logger.info(WITHDRAW_OF_CONTENT + currentTransaction.getAmount() + VALIDATED_END);
            BankAccount emitterBankAccount = getEmitter(currentTransaction);
            logger.info(WITHDRAW_OF_CONTENT + currentTransaction.getAmount()
                    + FROM_BANK_ACCOUNT_CONTENT + currentTransaction.getEmitterAccountId());

            metadata = new HashMap<>();
            metadata.put(EMITTER_AMOUNT_BEFORE_KEY, emitterBankAccount.getBalance().getAmount().toString());
            bankAccountService.withdrawAmountToAccount(currentTransaction, emitterBankAccount);
            metadata.put(EMITTER_AMOUNT_AFTER_KEY, emitterBankAccount.getBalance().getAmount().toString());
            Transaction completedTransaction = transactionStatusService.setStatus(currentTransaction, COMPLETED, metadata);
            bankAccountService.updateBankAccount(emitterBankAccount);
            return completedTransaction;
        } catch (TransactionException exception) {
            logger.error(TRANSACTION_START + currentTransaction.getId() + " of " + currentTransaction.getAmount() + " is invalid.");
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new TransactionException("Transaction error while withdrawing transaction: " + exception.getMessage());
        } catch (BankAccountException exception) {
            logger.error(TRANSACTION_START + currentTransaction.getId() + " withdraw refused for amount " + currentTransaction.getAmount() + ": " + exception.getMessage());
            metadata = Map.of(ERROR_KEY, exception.getMessage());
            transactionStatus = REFUSED;
            throw new BankAccountException("Bank account refused while withdrawing transaction: " + exception.getMessage());
        } finally {
            currentTransaction.setStatus(transactionStatus);
            transactionService.create(currentTransaction, metadata);
        }
    }
}

