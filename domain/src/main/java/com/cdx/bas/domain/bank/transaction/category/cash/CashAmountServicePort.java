package com.cdx.bas.domain.bank.transaction.category.cash;

public interface CashAmountServicePort {
    void applyToAccount (CashTransactionProcessingDetails cashTransactionProcessingDetails);
}
