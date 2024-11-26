package com.cdx.bas.application.bank.transaction.category.cash.type.withdraw;

import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.TransactionValidator;
import com.cdx.bas.domain.bank.transaction.category.cash.type.withdraw.WithdrawAmountServiceImpl;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class WithdrawProcessorFactory {
    private final TransactionValidator transactionValidator;
    private final TransactionStatusServicePort transactionStatusService;
    private final TransactionServicePort transactionService;
    private final BankAccountServicePort bankAccountService;
    private final WithdrawAmountServiceImpl withdrawAmountService;

    public WithdrawProcessorFactory(TransactionValidator transactionValidator,
                                    TransactionStatusServicePort transactionStatusService,
                                    TransactionServicePort transactionService,
                                    BankAccountServicePort bankAccountService, WithdrawAmountServiceImpl withdrawAmountService) {
        this.transactionValidator = transactionValidator;
        this.transactionStatusService = transactionStatusService;
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;
        this.withdrawAmountService = withdrawAmountService;
    }

    @Produces
    public WithdrawProcessorImpl createCreditProcessorService() {
        return new WithdrawProcessorImpl(transactionValidator, transactionStatusService, transactionService, bankAccountService, withdrawAmountService);
    }
}
