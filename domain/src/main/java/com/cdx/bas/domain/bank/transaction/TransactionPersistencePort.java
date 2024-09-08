package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;

import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public interface TransactionPersistencePort {
    
    /**
     * find Transaction from its id
     * 
     * @param id of Transaction
     * @return <Optional>Transaction if id corresponding or not to a Transaction
     */
    public Optional<Transaction> findById(long id);

    /**
     * find all transactions
     *
     * @return Set with all the transactions
     */
    public Set<Transaction> getAll();

    /**
     * find all transactions by status
     *
     * @return Set with all the transactions by status
     */
    public Set<Transaction> findAllByStatus(TransactionStatus transactionStatus);

    /**
     * find all unprocessed transactions in a queue
     * 
     * @return queue with all the unprocessed transactions
     */
    Queue<Transaction> findUnprocessedTransactions();
    
    /**
     * create the current Transaction
     *
     * @param transaction to create
     */
    public void create(Transaction transaction);
    
    /**
     * update the current Transaction
     * 
     * @param transaction to update
     * @return updated Transaction
     */
    public Transaction update(Transaction transaction);
    
    /**
     * delete current Transaction
     * 
     * @param id to remove
     */
    public Optional<Transaction> deleteById(long id);
}
