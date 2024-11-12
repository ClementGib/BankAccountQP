package com.cdx.bas.domain.bank.transaction.category.digital.type.debit;

import com.cdx.bas.domain.bank.transaction.TransactionException;
import com.cdx.bas.domain.bank.transaction.category.digital.DigitalAmountServicePort;
import com.cdx.bas.domain.bank.transaction.category.digital.DigitalTransactionProcessingDetails;
import com.cdx.bas.domain.currency.rate.ExchangeRateUtils;
import com.cdx.bas.domain.money.Money;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.cdx.bas.domain.message.CommonMessages.*;
import static com.cdx.bas.domain.message.MessageFormatter.format;
import static com.cdx.bas.domain.money.AmountUtils.isNotPositive;

@ApplicationScoped
public class DebitAmountServiceImpl implements DigitalAmountServicePort {

    @Override
    public void transferBetweenAccounts(DigitalTransactionProcessingDetails digitalTransactionProcessingDetails) {
        BigDecimal euroAmount = ExchangeRateUtils.getEuroAmountFrom(digitalTransactionProcessingDetails.getTransaction().getCurrency(),
                digitalTransactionProcessingDetails.getTransaction().getAmount());
        if (isNotPositive(euroAmount)) {
            throw new TransactionException(format(DEBIT_TRANSACTION_CONTEXT, DEBIT_ACTION, FAILED_STATUS,
                    Optional.of(SHOULD_HAVE_POSITIVE_VALUE_CAUSE),
                    List.of(TRANSACTION_ID_DETAIL + digitalTransactionProcessingDetails.getTransaction().getId(), EURO_AMOUNT_DETAIL + euroAmount)));
        }
        digitalTransactionProcessingDetails.getReceiverBankAccount().getBalance().minus(Money.of(euroAmount));
        digitalTransactionProcessingDetails.getEmitterBankAccount().getBalance().plus(Money.of(euroAmount));
    }
}
