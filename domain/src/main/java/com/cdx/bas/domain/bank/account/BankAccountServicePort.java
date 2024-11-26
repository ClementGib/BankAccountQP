package com.cdx.bas.domain.bank.account;

import com.cdx.bas.domain.bank.transaction.Transaction;

import java.util.List;

public interface BankAccountServicePort {

    /**
     * find all accounts
     *
     * @return List with all BankAccount
     */
    List<BankAccount> getAll();


    /**
     * find bank account from id
     *
     * @param bankAccountId
     * @return bank account found
     */
    BankAccount findBankAccount(Long bankAccountId);

    /**
     * add transaction to bank account
     *
     * @param transaction to add
     * @return bank account
     */
    BankAccount putTransaction(Transaction transaction, BankAccount bankAccount);

    /**
     * updated bank account
     *
     * @param bankAccount
     * @return bank account updated
     */
    BankAccount updateBankAccount(BankAccount bankAccount);
}
