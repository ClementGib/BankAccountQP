package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.bank.transaction.category.NewCashTransaction;
import com.cdx.bas.domain.bank.transaction.category.NewDigitalTransaction;

import java.util.Set;

public interface TransactionServicePort {

    /**
     * find all transactions
     *
     * @return Set with all the transactions
     */
    public Set<Transaction> getAll();

    /**
     * Create new transaction
     *
     * @param transaction to create
     */
    void create(Transaction transaction);

    /**
     * Update existing transaction
     *
     * @param transaction to update
     */
    void update(Transaction transaction);


    /**
     * find all transactions by status
     *
     * @return Set with all the transactions by status
     */
    public Set<Transaction> findAllByStatus(String status);


    /**
     * add digital transaction
     *
     * @param newDigitalTransaction to add
     */
    void createDigitalTransaction(NewDigitalTransaction newDigitalTransaction);

    /**
     * find Transaction from id
     *
     * @param transactionId
     * @return Transaction found
     */
    Transaction findTransaction(Long transactionId);

    /**
     * Process digital transaction
     *
     * @param digitalTransaction to process
     */
    void processDigitalTransaction(Transaction digitalTransaction);

    /**
     * Process deposit of cash
     *
     * @param newCashTransaction with detail of cash transaction
     */
    void deposit(NewCashTransaction newCashTransaction);

    /**
     * Process withdraw of cash
     *
     * @param newCashTransaction with detail of cash transaction
     */
    void withdraw(NewCashTransaction newCashTransaction);
}
