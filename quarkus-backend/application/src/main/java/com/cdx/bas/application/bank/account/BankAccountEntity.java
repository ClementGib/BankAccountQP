package com.cdx.bas.application.bank.account;

import java.math.BigDecimal;
import java.util.*;

import com.cdx.bas.application.transaction.TransactionEntity;
import jakarta.persistence.*;

import com.cdx.bas.application.customer.CustomerEntity;
import com.cdx.bas.domain.bank.account.AccountType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(schema = "basapp", name = "bank_accounts", uniqueConstraints = @UniqueConstraint(columnNames = "account_id"))
public class BankAccountEntity extends PanacheEntityBase {

    @Id
    @Column(name = "account_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_accounts_account_id_seq_gen")
    @SequenceGenerator(name = "bank_accounts_account_id_seq_gen", sequenceName = "bank_accounts_account_id_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @ManyToMany(mappedBy = "accounts", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<CustomerEntity> customers = new ArrayList<>();

    @OneToMany(mappedBy = "senderBankAccountEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date")
    private Set<TransactionEntity> issuedTransactions = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<CustomerEntity> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomerEntity> customers) {
        this.customers = customers;
    }

    public Set<TransactionEntity> getIssuedTransactions() {
        return issuedTransactions;
    }

    public void setIssuedTransactions(Set<TransactionEntity> issuedTransactions) {
        this.issuedTransactions = issuedTransactions;
    }

    public void addTransaction(TransactionEntity transaction) {
        this.issuedTransactions.add(transaction);
        transaction.setSenderBankAccountEntity(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccountEntity that = (BankAccountEntity) o;
        return Objects.equals(id, that.id) && type == that.type && Objects.equals(balance, that.balance) && Objects.equals(customers, that.customers) && Objects.equals(issuedTransactions, that.issuedTransactions);
    }
}
