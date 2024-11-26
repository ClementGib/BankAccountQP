package com.cdx.bas.domain.bank.transaction.category.digital;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.transaction.Transaction;

import java.util.Map;


public class DigitalTransactionProcessingDetails {
    Transaction transaction;
    BankAccount emitterBankAccount;
    BankAccount receiverBankAccount;
    Map<String, String> metadata;

    public DigitalTransactionProcessingDetails(Transaction transaction, BankAccount emitterBankAccount, BankAccount receiverBankAccount, Map<String, String> metadata) {
        this.transaction = transaction;
        this.emitterBankAccount = emitterBankAccount;
        this.receiverBankAccount = receiverBankAccount;
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

    public BankAccount getReceiverBankAccount() {
        return receiverBankAccount;
    }

    public void setReceiverBankAccount(BankAccount receiverBankAccount) {
        this.receiverBankAccount = receiverBankAccount;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
