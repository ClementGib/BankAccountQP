package com.cdx.bas.domain.bank.transaction.category.digital;

public interface DigitalAmountServicePort {
    void transferBetweenAccounts(DigitalTransactionProcessingDetails digitalTransactionProcessingDetails);
}
