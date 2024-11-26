package com.cdx.bas.domain.bank.transaction.category.cash.type.deposit;

import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.category.cash.CashAmountServicePort;
import com.cdx.bas.domain.bank.transaction.category.cash.CashTransactionProcessingDetails;
import com.cdx.bas.domain.currency.rate.ExchangeRateUtils;
import com.cdx.bas.domain.message.MessageFormatter;
import com.cdx.bas.domain.money.Money;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.money.AmountUtils.isNotPositive;


@ApplicationScoped
public class DepositAmountServiceImpl implements CashAmountServicePort {

    @Override
    public void applyToAccount(CashTransactionProcessingDetails cashTransactionProcessingDetails) {
        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(cashTransactionProcessingDetails.getTransaction().getCurrency(),
                cashTransactionProcessingDetails.getTransaction().getAmount());
        if (isNotPositive(euroAmount)) {
            throw new TransactionException(MessageFormatter.format(DEPOSIT_TRANSACTION_CONTEXT, DEPOSIT_ACTION, FAILED_STATUS,
                    Optional.of(SHOULD_HAVE_POSITIVE_VALUE_CAUSE),
                    List.of(TRANSACTION_ID_DETAIL + cashTransactionProcessingDetails.getTransaction().getId(), EURO_AMOUNT_DETAIL + euroAmount)));
        }
        cashTransactionProcessingDetails.getEmitterBankAccount().getBalance().plus(Money.of(euroAmount));
    }
}
