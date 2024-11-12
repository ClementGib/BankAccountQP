package com.cdx.bas.application.bank.transaction.category.digital.type.credit;

import com.cdx.bas.domain.bank.account.BankAccountServicePort;
import com.cdx.bas.domain.bank.transaction.TransactionServicePort;
import com.cdx.bas.domain.bank.transaction.category.digital.type.credit.CreditAmountServiceImpl;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatusServicePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreditProcessorFactory {
    private final TransactionStatusServicePort transactionStatusService;
    private final TransactionServicePort transactionService;
    private final BankAccountServicePort bankAccountService;
    private final CreditAmountServiceImpl creditAmountService;

    @Inject
    public CreditProcessorFactory(TransactionStatusServicePort transactionStatusService,
                                  TransactionServicePort transactionService,
                                  BankAccountServicePort bankAccountService,
                                  CreditAmountServiceImpl creditAmountService) {
        this.transactionStatusService = transactionStatusService;
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;
        this.creditAmountService = creditAmountService;
    }

    @Produces
    public CreditProcessorImpl createCreditProcessor() {
        return new CreditProcessorImpl(transactionStatusService, transactionService, bankAccountService, creditAmountService);
    }
}
