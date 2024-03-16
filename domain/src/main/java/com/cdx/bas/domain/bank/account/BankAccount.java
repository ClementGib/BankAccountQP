package com.cdx.bas.domain.bank.account;

import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.money.Money;
import com.cdx.bas.domain.bank.transaction.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BankAccount {

    @NotNull(message="id must not be null.")
	@Min(value=1, message="id must be positive and greater than 0.")
    protected Long id;
    
	@NotNull(message="type must not be null.")
	protected AccountType type;
    
    @NotNull(message="balance must not be null.")
	@Valid
	protected Money balance;
    
	@NotNull(message="customersId must not be null.")
	@Size(min=1, message="customersId must contains at least 1 customer id.")
	protected Set<Long> customersId = new HashSet<>();

    @NotNull(message="issued transactions must not be null.")
    protected Set<Transaction> issuedTransactions = new HashSet<>();
    
    public BankAccount(AccountType type) {
        this.type = type;
    }
}
