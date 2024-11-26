package com.cdx.bas.domain.bank.account.saving;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.money.Amount;
import com.cdx.bas.domain.money.Money;
import com.cdx.bas.domain.testing.Generated;

import java.util.Set;

import static com.cdx.bas.domain.bank.account.type.AccountType.*;

/**
 * Saving Account (French Livret A)
 */
public class SavingBankAccount extends BankAccount {

    @Override
    @Amount(min=1, max=22950, message="balance amount must be between 1 and 22950.")
    public Money    getBalance() {
        return super.balance;
    }

    @Generated
    public SavingBankAccount() {
        super(SAVING);
    }

    @Generated
    public SavingBankAccount(Long id, Money balance, Set<Long> customersId, Set<Transaction> issuedTransactions) {
        super(id, SAVING, balance, customersId, issuedTransactions);
    }
}
