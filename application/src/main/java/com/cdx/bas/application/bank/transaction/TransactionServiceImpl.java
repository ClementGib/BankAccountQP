package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.domain.bank.transaction.*;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.type.TransactionTypeProcessingServicePort;
import com.cdx.bas.domain.bank.transaction.validation.validator.TransactionValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;

import static com.cdx.bas.domain.bank.transaction.type.TransactionType.*;
import static com.cdx.bas.domain.text.MessageConstants.DEPOSIT_OF_CONTENT;
import static com.cdx.bas.domain.text.MessageConstants.WITHDRAW_OF_CONTENT;

@ApplicationScoped
public class TransactionServiceImpl implements TransactionServicePort {

    private final TransactionPersistencePort transactionRepository;

    private final TransactionValidator transactionValidator;

    private final TransactionTypeProcessingServicePort transactionTypeProcessingService;

    @Inject
    public TransactionServiceImpl(TransactionPersistencePort transactionRepository,
                                  TransactionValidator transactionValidator,
                                  TransactionTypeProcessingServicePort transactionTypeProcessingService) {
        this.transactionRepository = transactionRepository;
        this.transactionValidator = transactionValidator;
        this.transactionTypeProcessingService = transactionTypeProcessingService;
    }

    @Override
    @Transactional
    public Set<Transaction> getAll() {
        return transactionRepository.getAll();
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void create(Transaction transaction, Map<String, String> metadata) {
        transaction.setMetadata(metadata);
        transactionRepository.create(transaction);
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void update(Transaction transaction, Map<String, String> metadata) {
        transaction.getMetadata().putAll(metadata);
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
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
        return transaction;
    }

    @Override
    @Transactional
    public void processDigitalTransaction(Transaction digitalTransaction) {
        if (CREDIT.equals(digitalTransaction.getType())) {
            transactionTypeProcessingService.credit(digitalTransaction);
        } else if (DEPOSIT.equals(digitalTransaction.getType())) {
            transactionTypeProcessingService.deposit(digitalTransaction);
        }
    }

    @Override
    @Transactional
    public void deposit(NewCashTransaction newDepositTransaction) {
        Transaction depositTransaction = TransactionUtils.getNewCashTransaction(newDepositTransaction);
        depositTransaction.setType(DEPOSIT);
        depositTransaction.setLabel(DEPOSIT_OF_CONTENT + newDepositTransaction.amount() + StringUtils.SPACE + newDepositTransaction.currency());
        transactionTypeProcessingService.deposit(depositTransaction);
    }

    @Override
    @Transactional
    public void withdraw(NewCashTransaction newWithdrawTransaction) {
        Transaction withdrawTransaction = TransactionUtils.getNewCashTransaction(newWithdrawTransaction);
        withdrawTransaction.setType(WITHDRAW);
        withdrawTransaction.setLabel(WITHDRAW_OF_CONTENT + newWithdrawTransaction.amount() + StringUtils.SPACE + newWithdrawTransaction.currency());
        transactionTypeProcessingService.withdraw(withdrawTransaction);
    }
}
