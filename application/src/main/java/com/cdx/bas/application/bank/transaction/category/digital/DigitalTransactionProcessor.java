package com.cdx.bas.application.bank.transaction.category.digital;

import com.cdx.bas.application.bank.transaction.TransactionProcessorTemplate;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.category.digital.DigitalTransactionProcessingDetails;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.message.MessageFormatter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.COMPLETED;
import static com.cdx.bas.domain.message.CommonMessages.*;

@AllArgsConstructor
public abstract class DigitalTransactionProcessor extends TransactionProcessorTemplate {

    private static final Logger logger = LoggerFactory.getLogger(DigitalTransactionProcessor.class);

    private final TransactionStatusServicePort transactionStatusService;
    private final TransactionServicePort transactionService;
    private final BankAccountServicePort bankAccountService;

    protected abstract Transaction processType(DigitalTransactionProcessingDetails digitalTransactionProcessingDetails);
    protected abstract String formatError(Transaction transaction, String errorStatus, Exception exception);

    @Override
    protected Transaction processCategory(Transaction transaction, Map<String, String> metadata) {
        BankAccount emitterBankAccount = bankAccountService.findBankAccount(transaction.getEmitterAccountId());
        BankAccount receiverBankAccount = bankAccountService.findBankAccount(transaction.getReceiverAccountId());
        Transaction currentTransaction = transactionStatusService.setAsOutstanding(transaction);

        DigitalTransactionProcessingDetails digitalTransactionProcessingDetails = new DigitalTransactionProcessingDetails(currentTransaction,
                emitterBankAccount,
                receiverBankAccount,
                metadata);
        processType(digitalTransactionProcessingDetails);

        Transaction completedTransaction = transactionStatusService.setStatus(currentTransaction, COMPLETED, metadata);
        bankAccountService.updateBankAccount(emitterBankAccount);
        bankAccountService.updateBankAccount(receiverBankAccount);

        logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, CREDIT_ACTION, COMPLETED_STATUS,
                List.of(CREDIT_DETAIL + currentTransaction.getAmount(),
                        EMITTER_BANK_ACCOUNT_DETAIL + currentTransaction.getEmitterAccountId(),
                        RECEIVER_BANK_ACCOUNT_DETAIL + currentTransaction.getReceiverAccountId())));
        return completedTransaction;
    }

    @Override
    protected void persist(Transaction transaction) {
        transactionService.update(transaction);
    }
}
