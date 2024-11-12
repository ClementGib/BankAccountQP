package com.cdx.bas.domain.bank.account.mma;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.money.Amount;
import com.cdx.bas.domain.money.Money;
import com.cdx.bas.domain.testing.Generated;

import java.util.Set;

import static com.cdx.bas.domain.bank.account.type.AccountType.MMA;

/**
 * Money Market Account
 */
public class MMABankAccount extends BankAccount {

    @Override
    @Amount(min=1000, max=250000, message="balance amount must be between 1000 and 250000.")
    public Money getBalance() {
        return super.balance;
    }

    @Generated
    public MMABankAccount() {
        super(MMA);
    }

    @Generated
    public MMABankAccount(Long id, Money balance, Set<Long> customersId, Set<Transaction> issuedTransactions) {
        super(id, MMA, balance, customersId, issuedTransactions);
    }
}
