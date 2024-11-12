package com.cdx.bas.domain.bank.account;

import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.bank.transaction.Transaction;
import com.cdx.bas.domain.money.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @NotNull(message="issued transactions must not be null.")
    protected Set<Transaction> incomingTransactions = new HashSet<>();
    
    protected BankAccount(AccountType type) {
        this.type = type;
    }

    protected BankAccount() {
    }

    protected BankAccount(Long id, AccountType type, Money balance, Set<Long> customersId, Set<Transaction> issuedTransactions) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.customersId = customersId;
        this.issuedTransactions = issuedTransactions;
    }

    public @NotNull(message = "id must not be null.") @Min(value = 1, message = "id must be positive and greater than 0.") Long getId() {
        return id;
    }

    public void setId(@NotNull(message = "id must not be null.") @Min(value = 1, message = "id must be positive and greater than 0.") Long id) {
        this.id = id;
    }

    public @NotNull(message = "type must not be null.") AccountType getType() {
        return type;
    }

    public void setType(@NotNull(message = "type must not be null.") AccountType type) {
        this.type = type;
    }

    public @NotNull(message = "balance must not be null.") @Valid Money getBalance() {
        return balance;
    }

    public void setBalance(@NotNull(message = "balance must not be null.") @Valid Money balance) {
        this.balance = balance;
    }

    public @NotNull(message = "customersId must not be null.") @Size(min = 1, message = "customersId must contains at least 1 customer id.") Set<Long> getCustomersId() {
        return customersId;
    }

    public void setCustomersId(@NotNull(message = "customersId must not be null.") @Size(min = 1, message = "customersId must contains at least 1 customer id.") Set<Long> customersId) {
        this.customersId = customersId;
    }

    public @NotNull(message = "issued transactions must not be null.") Set<Transaction> getIssuedTransactions() {
        return issuedTransactions;
    }

    public void setIssuedTransactions(@NotNull(message = "issued transactions must not be null.") Set<Transaction> issuedTransactions) {
        this.issuedTransactions = issuedTransactions;
    }

    public @NotNull(message = "issued transactions must not be null.") Set<Transaction> getIncomingTransactions() {
        return incomingTransactions;
    }

    public void setIncomingTransactions(@NotNull(message = "issued transactions must not be null.") Set<Transaction> incomingTransactions) {
        this.incomingTransactions = incomingTransactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(id, that.id) && type == that.type && Objects.equals(balance, that.balance) && Objects.equals(customersId, that.customersId) && Objects.equals(issuedTransactions, that.issuedTransactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, balance, customersId, issuedTransactions);
    }
}
