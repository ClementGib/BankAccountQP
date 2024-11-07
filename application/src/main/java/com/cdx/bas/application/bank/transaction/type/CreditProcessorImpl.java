package com.cdx.bas.application.bank.transaction.type;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.BankAccountException;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.bank.transaction.TransactionException;

public class CreditProcessorImpl implements DigitalTransactionProcessor {
    @Override
    public void processDigital(Transaction transaction, BankAccount emitterBankAccount, BankAccount receiverBankAccount) throws TransactionException, BankAccountException {

    }
}
