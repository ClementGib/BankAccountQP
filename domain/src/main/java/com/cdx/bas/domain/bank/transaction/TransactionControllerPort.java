package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.bank.transaction.category.NewCashTransaction;
import com.cdx.bas.domain.bank.transaction.category.NewDigitalTransaction;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.Set;

public interface TransactionControllerPort {

    /**
     * Find all Transaction
     *
     * @return all Transaction found
     */
    Set<Transaction> getAll();

    /**
     * Find all Transaction with matching status
     *
     * @param status of Transaction
     * @return all Transaction corresponding to the status
     */
    Set<Transaction> getAllByStatus(@PathParam("status") String status) ;

    /**
     * Find Transaction from its id
     *
     * @param id of Transaction
     * @return Transaction corresponding to the id
     */
    Transaction findById(long id);

    /**
     * Create a new digital transaction
     *
     * @param newTransaction to add to a BankAccount
     * @return Response with status corresponding to transaction validation or not
     */
    Response addDigitalTransaction(NewDigitalTransaction newTransaction);

    /**
     * Process deposit of cash
     *
     * @param newCashTransaction with detail of cash transaction
     * @return Response with status corresponding to transaction validation or not
     */
    Response deposit(NewCashTransaction newCashTransaction);

    /**
     * Process deposit of cash
     *
     * @param newCashTransaction with detail of cash transaction
     * @return Response with status corresponding to transaction validation or not
     */
    Response withdraw(NewCashTransaction newCashTransaction);


}
