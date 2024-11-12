package com.cdx.bas.domain.bank.account.checking;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.money.Amount;
import com.cdx.bas.domain.money.Money;
import com.cdx.bas.domain.testing.Generated;

import java.util.Set;

import static com.cdx.bas.domain.bank.account.type.AccountType.CHECKING;

/**
 * Checking account (transaction account/current account)
 */
public class CheckingBankAccount extends BankAccount {

    @Override
    @Amount(min=-600, max=100000, message="balance amount must be between -600 and 100000.")
    public Money getBalance() {
        return super.balance;
    }

    @Generated
    public CheckingBankAccount() {
        super(CHECKING);
    }

    @Generated
    public CheckingBankAccount(Long id, Money balance, Set<Long> customersId, Set<Transaction> issuedTransactions) {
        super(id, CHECKING, balance, customersId, issuedTransactions);
    }

}
