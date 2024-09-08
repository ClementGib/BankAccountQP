package com.cdx.bas.application.bank.transaction;

import com.cdx.bas.application.bank.account.BankAccountEntity;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.type.TransactionType;
import io.hypersistence.utils.hibernate.type.json.JsonStringType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(schema = "basapp", name = "transactions", uniqueConstraints = @UniqueConstraint(columnNames = "transaction_id"))
@NamedQueries(@NamedQuery(
        name = "TransactionEntity.findUnprocessed", query = "SELECT t FROM TransactionEntity t WHERE t.status = :status ORDER BY t.date ASC"))
public class TransactionEntity extends PanacheEntityBase {

    @Id
    @Column(name = "transaction_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_transaction_id_seq_gen")
    @SequenceGenerator(name = "transactions_transaction_id_seq_gen", sequenceName = "transactions_transaction_id_seq", allocationSize = 1, initialValue = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emitter_account_id", nullable = false)
    private BankAccountEntity emitterBankAccountEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_account_id")
    private BankAccountEntity receiverBankAccountEntity;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "label", nullable = false)
    private String label;

    @Type(JsonStringType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String metadata;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankAccountEntity getEmitterBankAccountEntity() {
        return emitterBankAccountEntity;
    }

    public void setEmitterBankAccountEntity(BankAccountEntity emitterBankAccountEntity) {
        this.emitterBankAccountEntity = emitterBankAccountEntity;
    }

    public BankAccountEntity getReceiverBankAccountEntity() {
        return receiverBankAccountEntity;
    }

    public void setReceiverBankAccountEntity(BankAccountEntity receiverBankAccountEntity) {
        this.receiverBankAccountEntity = receiverBankAccountEntity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Objects.equals(id, that.id)
                && Objects.equals(emitterBankAccountEntity, that.emitterBankAccountEntity)
                && Objects.equals(receiverBankAccountEntity, that.receiverBankAccountEntity)
                && Objects.equals(amount, that.amount)
                && Objects.equals(currency, that.currency)
                & type == that.type && status == that.status
                && Objects.equals(date, that.date)
                && Objects.equals(label, that.label)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, emitterBankAccountEntity, receiverBankAccountEntity, amount, currency, type, status, date, label, metadata);
    }
}
