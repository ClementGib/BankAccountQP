package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.application.bank.transaction.category.cash.type.deposit.DepositProcessorImpl;
import com.cdx.bas.application.bank.transaction.category.cash.type.withdraw.WithdrawProcessorImpl;
import com.cdx.bas.application.bank.transaction.category.digital.type.credit.CreditProcessorImpl;
import com.cdx.bas.application.bank.transaction.category.digital.type.debit.DebitProcessorImpl;
import com.cdx.bas.domain.bank.transaction.*;
import com.cdx.bas.domain.bank.transaction.category.NewCashTransaction;
import com.cdx.bas.domain.bank.transaction.category.NewDigitalTransaction;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.message.CommonMessages;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType.*;
import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.message.MessageFormatter.format;

@ApplicationScoped
public class TransactionServiceImpl implements TransactionServicePort {

    private final TransactionPersistencePort transactionRepository;
    private final TransactionValidator transactionValidator;
    private final CreditProcessorImpl creditProcessorService;
    private final DebitProcessorImpl debitProcessorService;
    private final DepositProcessorImpl depositProcessorService;
    private final WithdrawProcessorImpl withdrawProcessorService;

    @Inject
    public TransactionServiceImpl(TransactionPersistencePort transactionRepository,
                                  TransactionValidator transactionValidator,
                                  CreditProcessorImpl creditProcessorService,
                                  DebitProcessorImpl debitProcessorService,
                                  DepositProcessorImpl depositProcessorService,
                                  WithdrawProcessorImpl withdrawProcessorService) {
        this.transactionRepository = transactionRepository;
        this.transactionValidator = transactionValidator;
        this.creditProcessorService = creditProcessorService;
        this.debitProcessorService = debitProcessorService;
        this.depositProcessorService = depositProcessorService;
        this.withdrawProcessorService = withdrawProcessorService;
    }

    @Override
    @Transactional
    public Set<Transaction> getAll() {
        return transactionRepository.getAll();
    }

    @Override
    public void create(Transaction transaction) {
        transactionRepository.create(transaction);
    }

    @Override
    public void update(Transaction transaction) {
        transactionRepository.update(transaction);
    }

    @Override
    @Transactional
    public Set<Transaction> findAllByStatus(String status) throws IllegalArgumentException {
        TransactionStatus transactionStatus = TransactionStatus.fromString(status);
        return transactionRepository.findAllByStatus(transactionStatus);
    }

    @Override
    @Transactional
    public void createDigitalTransaction(NewDigitalTransaction newDigitalTransaction) throws TransactionException {
        Transaction digitalTransaction = TransactionUtils.getNewDigitalTransaction(newDigitalTransaction);
        transactionValidator.validateNewDigitalTransaction(digitalTransaction);
        transactionRepository.create(digitalTransaction);
    }

    @Override
    @Transactional
    public Transaction findTransaction(Long transactionId) {
        if (transactionId == null) {
            throw new TransactionException(format(BANK_ACCOUNT_CONTEXT, SEARCHING_ACTION, FAILED_STATUS,
                    Optional.of(MISSING_ID_CAUSE), List.of(BANK_ACCOUNT_ID_DETAIL + "null")));
        }

        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException(format(TRANSACTION_CONTEXT, SEARCHING_ACTION, FAILED_STATUS,
                        Optional.of(NOT_FOUND_CAUSE), List.of(TRANSACTION_ID_DETAIL + transactionId))));
    }

    @Override
    @Transactional
    public void processDigitalTransaction(Transaction digitalTransaction) {
        if (CREDIT.equals(digitalTransaction.getType())) {
            creditProcessorService.processTransaction(digitalTransaction);
        } else if (DEBIT.equals(digitalTransaction.getType())) {
            debitProcessorService.processTransaction(digitalTransaction);
        }
    }

    @Override
    @Transactional
    public void deposit(NewCashTransaction newDepositTransaction) {
        Transaction depositTransaction = TransactionUtils.getNewCashTransaction(newDepositTransaction);
        depositTransaction.setType(DEPOSIT);
        depositTransaction.setLabel(DEPOSIT_DETAIL + newDepositTransaction.amount() + StringUtils.SPACE + newDepositTransaction.currency());
        depositProcessorService.processTransaction(depositTransaction);
    }

    @Override
    @Transactional
    public void withdraw(NewCashTransaction newWithdrawTransaction) {
        Transaction withdrawTransaction = TransactionUtils.getNewCashTransaction(newWithdrawTransaction);
        withdrawTransaction.setType(WITHDRAW);
        withdrawTransaction.setLabel(CommonMessages.WITHDRAW_DETAIL + newWithdrawTransaction.amount() + StringUtils.SPACE + newWithdrawTransaction.currency());
        withdrawProcessorService.processTransaction(withdrawTransaction);
    }
}
