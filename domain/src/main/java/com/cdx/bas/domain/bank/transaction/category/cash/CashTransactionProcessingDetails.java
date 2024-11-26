package com.cdx.bas.domain.bank.transaction.category.cash;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;

import java.util.Map;


public class CashTransactionProcessingDetails {
    Transaction transaction;
    BankAccount emitterBankAccount;
    Map<String, String> metadata;

    public CashTransactionProcessingDetails(Transaction transaction, BankAccount emitterBankAccount, Map<String, String> metadata) {
        this.transaction = transaction;
        this.emitterBankAccount = emitterBankAccount;
        this.metadata = metadata;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public BankAccount getEmitterBankAccount() {
        return emitterBankAccount;
    }

    public void setEmitterBankAccount(BankAccount emitterBankAccount) {
        this.emitterBankAccount = emitterBankAccount;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
