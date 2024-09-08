package com.cdx.bas.domain.bank.account.mma;

import com.cdx.bas.domain.bank.account.BankAccount;
import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.money.Amount;
import com.cdx.bas.domain.money.Money;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

import static com.cdx.bas.domain.bank.account.type.AccountType.*;

/**
 * Money Market Account
 */
@SuperBuilder
public class MMABankAccount extends BankAccount {

    @Amount(min=1000, max=250000, message="balance amount must be between 1000 and 250000.")
    public Money getBalance() {
        return super.balance;
    }
    
    public MMABankAccount() {
        super(MMA);
    }

    public MMABankAccount(Long id, Money balance, Set<Long> customersId, Set<Transaction> issuedTransactions) {
        super(id, MMA, balance, customersId, issuedTransactions);
    }
}
