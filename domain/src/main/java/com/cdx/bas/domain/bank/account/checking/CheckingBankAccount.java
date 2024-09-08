package com.cdx.bas.domain.bank.account.checking;

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
 * Checking account (transaction account/current account)
 */
@SuperBuilder
public class CheckingBankAccount extends BankAccount {

    @Amount(min=-600, max=100000, message="balance amount must be between -600 and 100000.")
    public Money getBalance() {
        return super.balance;
    }
    
    public CheckingBankAccount() {
        super(CHECKING);
    }

    public CheckingBankAccount(Long id, Money balance, Set<Long> customersId, Set<Transaction> issuedTransactions) {
        super(id, CHECKING, balance, customersId, issuedTransactions);
    }

}
