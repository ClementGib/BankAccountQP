package com.cdx.bas.application.bank.transaction.category.cash;

import com.cdx.bas.application.bank.transaction.TransactionProcessorTemplate;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.TransactionValidator;
import com.cdx.bas.domain.bank.transaction.category.cash.CashTransactionProcessingDetails;
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
public abstract class CashAmountService extends TransactionProcessorTemplate {

    private static final Logger logger = LoggerFactory.getLogger(CashAmountService.class);

    private final TransactionValidator transactionValidator;
    private final TransactionStatusServicePort transactionStatusService;
    private final TransactionServicePort transactionService;
    private final BankAccountServicePort bankAccountService;

    protected abstract Transaction processType(CashTransactionProcessingDetails cashTransactionProcessingDetails);
    protected abstract String formatError(Transaction transaction, String errorStatus, Exception exception);

    @Override
    protected Transaction processCategory(Transaction transaction, Map<String, String> metadata) {
        transactionValidator.validateCashTransaction(transaction);
        BankAccount emitterBankAccount = bankAccountService.findBankAccount(transaction.getEmitterAccountId());

        CashTransactionProcessingDetails cashTransactionProcessingDetails = new CashTransactionProcessingDetails(transaction, emitterBankAccount, metadata);
        processType(cashTransactionProcessingDetails);

        Transaction completedTransaction = transactionStatusService.setStatus(transaction, COMPLETED, metadata);
        bankAccountService.updateBankAccount(emitterBankAccount);

        logger.debug(MessageFormatter.format(TRANSACTION_CONTEXT, CASH_TRANSACTION_ACTION, COMPLETED_STATUS,
                List.of(TRANSACTION_ID_DETAIL + transaction.getId(),
                        CREDIT_DETAIL + transaction.getAmount(),
                        EMITTER_BANK_ACCOUNT_DETAIL + transaction.getEmitterAccountId(),
                        RECEIVER_BANK_ACCOUNT_DETAIL + transaction.getReceiverAccountId())));
        return completedTransaction;
    }

    @Override
    protected void persist(Transaction transaction) {
        transactionService.create(transaction);
    }
}
