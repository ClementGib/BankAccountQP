package com.cdx.bas.application.bank.transaction.category.digital.type.debit;

import com.cdx.bas.application.bank.transaction.category.digital.DigitalTransactionProcessor;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.category.digital.DigitalTransactionProcessingDetails;
import com.cdx.bas.domain.bank.transaction.category.digital.type.debit.DebitAmountServiceImpl;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import com.cdx.bas.domain.message.MessageFormatter;

import java.util.List;
import java.util.Optional;

import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.metadata.MetadataFieldNames.*;

public class DebitProcessorImpl extends DigitalTransactionProcessor {

    private final DebitAmountServiceImpl debitAmountService;

    public DebitProcessorImpl(TransactionStatusServicePort transactionStatusService, TransactionServicePort transactionService, BankAccountServicePort bankAccountService, DebitAmountServiceImpl debitAmountService) {
        super(transactionStatusService, transactionService, bankAccountService);
        this.debitAmountService = debitAmountService;
    }

    @Override
    protected Transaction processType(DigitalTransactionProcessingDetails digitalTransactionProcessingDetails) {
        digitalTransactionProcessingDetails.getMetadata().put(EMITTER_AMOUNT_BEFORE_KEY, digitalTransactionProcessingDetails.getEmitterBankAccount().getBalance().getAmount().toString());
        digitalTransactionProcessingDetails.getMetadata().put(RECEIVER_AMOUNT_BEFORE_KEY, digitalTransactionProcessingDetails.getReceiverBankAccount().getBalance().getAmount().toString());
        debitAmountService.transferBetweenAccounts(digitalTransactionProcessingDetails);
        digitalTransactionProcessingDetails.getMetadata().put(EMITTER_AMOUNT_AFTER_KEY, digitalTransactionProcessingDetails.getEmitterBankAccount().getBalance().getAmount().toString());
        digitalTransactionProcessingDetails.getMetadata().put(RECEIVER_AMOUNT_AFTER_KEY, digitalTransactionProcessingDetails.getReceiverBankAccount().getBalance().getAmount().toString());
        return digitalTransactionProcessingDetails.getTransaction();
    }

    @Override
    protected String formatError(Transaction transaction, String errorStatus, Exception exception) {
        return MessageFormatter.format(DEBIT_TRANSACTION_CONTEXT, DEBIT_ACTION, errorStatus,
                Optional.of(DOMAIN_ERROR),
                List.of(TRANSACTION_ID_DETAIL + transaction.getId(),
                        ERROR_DETAIL + exception.getMessage()));
    }
}
