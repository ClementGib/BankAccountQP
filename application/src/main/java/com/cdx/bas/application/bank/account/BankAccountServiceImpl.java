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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.message.MessageFormatter.format;
import static com.cdx.bas.domain.money.AmountUtils.isNotPositive;

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
        return bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new BankAccountException(format(BANK_ACCOUNT_CONTEXT, UPDATE_ACTION, FAILED_STATUS,
                        Optional.of(NOT_FOUND_CAUSE), List.of(ID_DETAIL + bankAccountId))));
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

    @Override
    public void creditAmountToAccounts(Transaction transaction, BankAccount emitterBankAccount, BankAccount receiverBankAccount) {
        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(transaction.getCurrency(), transaction.getAmount());
        if (isNotPositive(euroAmount)) {
            throw new TransactionException(format(CREDIT_TRANSACTION_CONTEXT, CREDIT_ACTION, FAILED_STATUS,
                    Optional.of(SHOULD_HAVE_POSITIVE_VALUE_CAUSE),
                    List.of(ID_DETAIL + transaction.getId(), EURO_AMOUNT_DETAIL + euroAmount)));
        }
        emitterBankAccount.getBalance().minus(Money.of(euroAmount));
        receiverBankAccount.getBalance().plus(Money.of(euroAmount));
    }

    @Override
    public void depositAmountToAccount(Transaction transaction, BankAccount emitterBankAccount) {
        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(transaction.getCurrency(), transaction.getAmount());
        if (isNotPositive(euroAmount)) {
            throw new TransactionException(format(DEBIT_TRANSACTION_CONTEXT, DEBIT_ACTION, FAILED_STATUS,
                    Optional.of(SHOULD_HAVE_POSITIVE_VALUE_CAUSE),
                    List.of(ID_DETAIL + transaction.getId(), EURO_AMOUNT_DETAIL + euroAmount)));
        }
        emitterBankAccount.getBalance().plus(Money.of(euroAmount));
    }

    @Override
    public void withdrawAmountToAccount(Transaction transaction, BankAccount emitterBankAccount) {
        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(transaction.getCurrency(), transaction.getAmount());
        if (isNotPositive(euroAmount)) {
            throw new TransactionException(format(WITHDRAW_TRANSACTION_CONTEXT, WITHDRAW_ACTION, FAILED_STATUS,
                    Optional.of(SHOULD_HAVE_POSITIVE_VALUE_CAUSE),
                    List.of(ID_DETAIL + transaction.getId(), EURO_AMOUNT_DETAIL + euroAmount)));
        }
        emitterBankAccount.getBalance().minus(Money.of(euroAmount));
    }


}
