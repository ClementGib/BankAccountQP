package com.cdx.bas.application.bank.transaction.category.digital.type.debit;

import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.category.digital.type.debit.DebitAmountServiceImpl;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class DebitProcessorFactory {
    private final TransactionStatusServicePort transactionStatusService;
    private final TransactionServicePort transactionService;
    private final BankAccountServicePort bankAccountService;
    private final DebitAmountServiceImpl debitAmountService;

    public DebitProcessorFactory(TransactionStatusServicePort transactionStatusService,
                                 TransactionServicePort transactionService,
                                 BankAccountServicePort bankAccountService,
                                 DebitAmountServiceImpl debitAmountService) {
        this.transactionStatusService = transactionStatusService;
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;
        this.debitAmountService = debitAmountService;
    }

    @Produces
    public DebitProcessorImpl createCreditProcessor() {
        return new DebitProcessorImpl(transactionStatusService, transactionService, bankAccountService, debitAmountService);
    }
}
