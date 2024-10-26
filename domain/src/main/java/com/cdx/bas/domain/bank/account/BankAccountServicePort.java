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
     * add transaction to bank account
     *
     * @param transaction to post with amount and currency
     * @param emitterBankAccount which emits transaction
     * @param receiverBankAccount which receives transaction
     */
    void creditAmountToAccounts(Transaction transaction, BankAccount emitterBankAccount, BankAccount receiverBankAccount) ;

    /**
     * updated bank account
     *
     * @param bankAccount
     * @return bank account updated
     */
    BankAccount updateBankAccount(BankAccount bankAccount);

    void depositAmountToAccount(Transaction transaction, BankAccount emitterBankAccount);

    void withdrawAmountToAccount(Transaction transaction, BankAccount emitterBankAccount);
}
