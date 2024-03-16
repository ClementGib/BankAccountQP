package com.cdx.bas.application.bank.account;

import com.cdx.bas.application.bank.transaction.TransactionUtils;
import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.account.BankAccountPersistencePort;
import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.account.validation.BankAccountValidator;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.currency.rate.ExchangeRateUtils;
import com.cdx.bas.domain.money.Money;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.cdx.bas.domain.money.AmountUtils.isNotPositive;
import static com.cdx.bas.domain.text.MessageConstants.*;

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
    public BankAccount findBankAccount(Long bankAccountId){
        return bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new BankAccountException("Missing bank account with id: " + bankAccountId));
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
        logger.debug("update bank account" + bankAccount.getId());
        bankAccountValidator.validateBankAccount(bankAccount);
        return bankAccountRepository.update(bankAccount);
    }

    @Override
    public void creditAmountToAccounts(Transaction transaction, BankAccount emitterBankAccount, BankAccount receiverBankAccount) {
        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(transaction.getCurrency(), transaction.getAmount());
        if (isNotPositive(euroAmount)) {
            throw new TransactionException(CREDIT_TRANSACTION_START + transaction.getId()
                    + SHOULD_HAVE_POSITIVE_VALUE_CONTENT + euroAmount);
        }
        emitterBankAccount.getBalance().minus(Money.of(euroAmount));
        receiverBankAccount.getBalance().plus(Money.of(euroAmount));
        logger.debug(ADD_AMOUNT_START + emitterBankAccount.getBalance()
                + StringUtils.SPACE + transaction.getCurrency()
                + FROM_BANK_ACCOUNT_CONTENT + emitterBankAccount.getId()
                + TO_BANK_ACCOUNT_CONTENT + receiverBankAccount.getId());
    }

    @Override
    public void depositAmountToAccount(Transaction transaction, BankAccount emitterBankAccount) {
        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(transaction.getCurrency(), transaction.getAmount());
        if (isNotPositive(euroAmount)) {
            throw new TransactionException(DEPOSIT_TRANSACTION_START + transaction.getId()
                    + SHOULD_HAVE_POSITIVE_VALUE_CONTENT + euroAmount);
        }
        emitterBankAccount.getBalance().plus(Money.of(euroAmount));
        logger.debug(ADD_AMOUNT_START + emitterBankAccount.getBalance()
                + StringUtils.SPACE + transaction.getCurrency()
                + TO_BANK_ACCOUNT_CONTENT + emitterBankAccount.getId());
    }

    @Override
    public void withdrawAmountToAccount(Transaction transaction, BankAccount emitterBankAccount) {
        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(transaction.getCurrency(), transaction.getAmount());
        if (isNotPositive(euroAmount)) {
            throw new TransactionException(WITHDRAW_TRANSACTION_START + transaction.getId()
                    + SHOULD_HAVE_POSITIVE_VALUE_CONTENT + euroAmount);
        }
        emitterBankAccount.getBalance().minus(Money.of(euroAmount));
        logger.debug(ADD_AMOUNT_START + emitterBankAccount.getBalance()
                + StringUtils.SPACE + transaction.getCurrency()
                + TO_BANK_ACCOUNT_CONTENT + emitterBankAccount.getId());
    }



}
