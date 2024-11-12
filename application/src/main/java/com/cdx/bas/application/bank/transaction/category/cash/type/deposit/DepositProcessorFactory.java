package com.cdx.bas.application.bank.transaction.category.cash.type.deposit;

import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.TransactionValidator;
import com.cdx.bas.domain.bank.transaction.category.cash.type.deposit.DepositAmountServiceImpl;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class DepositProcessorFactory {
    private final TransactionValidator transactionValidator;
    private final TransactionStatusServicePort transactionStatusService;
    private final TransactionServicePort transactionService;
    private final BankAccountServicePort bankAccountService;
    private final DepositAmountServiceImpl depositAmountService;

    public DepositProcessorFactory(TransactionValidator transactionValidator,
                                   TransactionStatusServicePort transactionStatusService,
                                   TransactionServicePort transactionService,
                                   BankAccountServicePort bankAccountService, DepositAmountServiceImpl depositAmountService) {
        this.transactionValidator = transactionValidator;
        this.transactionStatusService = transactionStatusService;
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;
        this.depositAmountService = depositAmountService;
    }

    @Produces
    public DepositProcessorImpl createCreditProcessorService() {
        return new DepositProcessorImpl(transactionValidator, transactionStatusService, transactionService, bankAccountService, depositAmountService);
    }
}
