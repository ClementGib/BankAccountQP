package com.cdx.bas.application.bank.account;

import com.cdx.bas.application.bank.customer.CustomerEntity;
import com.cdx.bas.application.bank.transaction.TransactionEntity;
import com.cdx.bas.domain.bank.account.type.AccountType;
import com.cdx.bas.domain.testing.Generated;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
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

    @ToString.Exclude
    @JsonIgnore
    @ManyToMany(mappedBy = "accounts", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    private Set<CustomerEntity> customers = new HashSet<>();

    @OneToMany(mappedBy = "emitterBankAccountEntity", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("date")
    private Set<TransactionEntity> issuedTransactions = new HashSet<>();

    @OneToMany(mappedBy = "receiverBankAccountEntity")
    @OrderBy("date")
    private Set<TransactionEntity> incomingTransactions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccountEntity that = (BankAccountEntity) o;
        return Objects.equals(id, that.id)
                && type == that.type
                && Objects.equals(balance, that.balance)
                && Objects.equals(customers, that.customers)
                && Objects.equals(issuedTransactions, that.issuedTransactions)
                && Objects.equals(incomingTransactions, that.incomingTransactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, balance);
    }
}
