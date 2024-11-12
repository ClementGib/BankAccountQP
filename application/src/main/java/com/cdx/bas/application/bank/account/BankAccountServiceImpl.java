package com.cdx.bas.application.bank.account;

import com.cdx.bas.application.bank.transaction.TransactionUtils;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.BankAccountPersistencePort;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.account.validation.BankAccountValidator;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.message.MessageFormatter.format;

@ApplicationScoped
public class BankAccountServiceImpl implements BankAccountServicePort {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountServiceImpl.class);

    BankAccountPersistencePort bankAccountRepository;

    BankAccountValidator bankAccountValidator;

    TransactionServicePort transactionService;

    @Inject
    public BankAccountServiceImpl(BankAccountPersistencePort bankAccountRepository,
                                  BankAccountValidator bankAccountValidator,
                                  TransactionServicePort transactionService) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountValidator = bankAccountValidator;
        this.transactionService = transactionService;
    }

    @Override
    @Transactional
    public List<BankAccount> getAll() {
        return bankAccountRepository.getAll();
    }

    @Override
    @Transactional
    public BankAccount findBankAccount(Long bankAccountId) {
        if (bankAccountId == null) {
            throw new BankAccountException(format(BANK_ACCOUNT_CONTEXT, SEARCHING_ACTION, FAILED_STATUS,
                    Optional.of(MISSING_ID_CAUSE), List.of(BANK_ACCOUNT_ID_DETAIL + "null")));
        }

        return bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new BankAccountException(format(BANK_ACCOUNT_CONTEXT, SEARCHING_ACTION, FAILED_STATUS,
                        Optional.of(NOT_FOUND_CAUSE), List.of(BANK_ACCOUNT_ID_DETAIL + bankAccountId))));
    }

    @Override
    @Transactional
    public BankAccount putTransaction(Transaction transaction, BankAccount bankAccount) {
        Optional<Transaction> optionalStoredTransaction = bankAccount.getIssuedTransactions().stream()
                .filter(actualTransaction -> actualTransaction.getId().equals(transaction.getId()))
                .findFirst();

        if (optionalStoredTransaction.isPresent()) {
            Transaction mergedTransaction = TransactionUtils.mergeTransactions(optionalStoredTransaction.get(), transaction);
            bankAccount.getIssuedTransactions()
                    .removeIf(existingTransaction -> existingTransaction.getId().equals(mergedTransaction.getId()));
            bankAccount.getIssuedTransactions().add(mergedTransaction);
        } else {
            bankAccount.getIssuedTransactions().add(transaction);
        }
        return bankAccount;
    }

    @Override
    @Transactional
    public BankAccount updateBankAccount(BankAccount bankAccount) throws BankAccountException {
        bankAccountValidator.validateBankAccount(bankAccount);
        BankAccount updatedBankAccount = bankAccountRepository.update(bankAccount);
        logger.debug(format(BANK_ACCOUNT_CONTEXT, UPDATE_ACTION, SUCCESS_STATUS));
        return updatedBankAccount;
    }
}