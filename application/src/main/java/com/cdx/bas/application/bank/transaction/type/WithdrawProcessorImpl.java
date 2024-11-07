package com.cdx.bas.application.bank.transaction.type;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;

public class WithdrawProcessorImpl implements PhysicalCashTransactionProcessor{
    @Override
    public void processPhysicalCash(Transaction transaction, BankAccount emitterBankAccount) throws TransactionException, BankAccountException {

    }
}
