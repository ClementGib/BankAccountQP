package com.cdx.bas.domain.bank.transaction.type;

import com.cdx.bas.domain.bank.transaction.Transaction;

public interface TransactionProcessorServicePort {

    /**
     * Credit bank account with transaction according to its amount
     *
     * @param transaction to process
     */
    Transaction credit(Transaction transaction);

    /**
     * Debit bank account with transaction according to its amount
     *
     * @param transaction to process
     */
    Transaction debit(Transaction transaction);

    /**
     * Deposit amount to a corresponding bank account
     *
     * @param transaction to process
     */
    Transaction deposit(Transaction transaction);

    /**
     * Withdraw amount to a corresponding bank account
     *
     * @param transaction to process
     */
    Transaction withdraw(Transaction transaction);
}
