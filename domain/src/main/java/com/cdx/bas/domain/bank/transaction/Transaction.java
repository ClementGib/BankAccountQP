package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;
import com.cdx.bas.domain.bank.transaction.category.digital.type.ValidType;
import com.cdx.bas.domain.bank.transaction.category.group.*;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.status.ValidStatus;
import com.cdx.bas.domain.currency.validation.ValidCurrency;
import com.cdx.bas.domain.testing.Generated;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType.*;
import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;

@Generated
public class Transaction implements Comparable<Transaction> {

    @Min(value = 1, message = "Id must be positive and greater than 0 for existing transaction.", groups = ExistingTransactionGroup.class)
    @NotNull(message = "Id must not be null for existing transaction.", groups = ExistingTransactionGroup.class)
    @Null(message = "Id must be null for new transaction.", groups = NewTransactionGroup.class)
    @Positive(message = "Id must be positive.", groups = AdvancedGroup.class)
    private Long id;

    @Positive(message = "Emitter account id  must be positive.", groups = AdvancedGroup.class)
    @NotNull(message = "Emitter account id must not be null.")
    private Long emitterAccountId;

    @Null(message = "Receiver account id must be null for cash movement.", groups = PhysicalCashTransactionGroup.class)
    @Positive(message = "Receiver account id  must be positive.", groups = AdvancedGroup.class)
    @NotNull(message = "Receiver account id must not be null.", groups = DigitalTransactionGroup.class)
    private Long receiverAccountId;

    @Min(value = 10, message = "Amount must be greater than 10 for cash movement.", groups = PhysicalCashTransactionGroup.class)
    @Min(value = 1, message = "Amount must be positive and greater than 0.", groups = DigitalTransactionGroup.class)
    @NotNull(message = "Amount must not be null.")
    private BigDecimal amount;

    @ValidCurrency(groups = AdvancedGroup.class)
    @NotNull(message = "Currency must not be null.")
    private String currency;

    @ValidType(expectedTypes = {CREDIT, DEBIT}, groups = DigitalTransactionGroup.class)
    @ValidType(expectedTypes = {DEPOSIT, WITHDRAW}, groups = PhysicalCashTransactionGroup.class)
    @NotNull(message = "Type must not be null.")
    private TransactionType type;

    @ValidStatus(expectedStatus = UNPROCESSED, groups = NewTransactionGroup.class)
    @NotNull(message = "Status must not be null.")
    private TransactionStatus status;

    @NotNull(message = "Date must not be null.")
    private Instant date;

    @NotNull(message = "Label must not be null.")
    private String label;

    @NotEmpty(message = "Bill must be define for cash movements.", groups = PhysicalCashTransactionGroup.class)
    @NotNull(message = "Metadata must not be null.")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public int compareTo(Transaction transactionToCompare) {
        return this.getDate().compareTo(transactionToCompare.getDate());
    }

    public Transaction() {
    }

    public Transaction(Long id, Long emitterAccountId, Long receiverAccountId, BigDecimal amount, String currency, TransactionType type, TransactionStatus status, Instant date, String label, Map<String, String> metadata) {
        this.id = id;
        this.emitterAccountId = emitterAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.status = status;
        this.date = date;
        this.label = label;
        this.metadata = metadata;
    }

    public @Min(value = 1, message = "Id must be positive and greater than 0 for existing transaction.", groups = ExistingTransactionGroup.class) @NotNull(message = "Id must not be null for existing transaction.", groups = ExistingTransactionGroup.class) @Null(message = "Id must be null for new transaction.", groups = NewTransactionGroup.class) @Positive(message = "Id must be positive.", groups = AdvancedGroup.class) Long getId() {
        return id;
    }

    public void setId(@Min(value = 1, message = "Id must be positive and greater than 0 for existing transaction.", groups = ExistingTransactionGroup.class) @NotNull(message = "Id must not be null for existing transaction.", groups = ExistingTransactionGroup.class) @Null(message = "Id must be null for new transaction.", groups = NewTransactionGroup.class) @Positive(message = "Id must be positive.", groups = AdvancedGroup.class) Long id) {
        this.id = id;
    }

    public @Positive(message = "Emitter account id  must be positive.", groups = AdvancedGroup.class) @NotNull(message = "Emitter account id must not be null.") Long getEmitterAccountId() {
        return emitterAccountId;
    }

    public void setEmitterAccountId(@Positive(message = "Emitter account id  must be positive.", groups = AdvancedGroup.class) @NotNull(message = "Emitter account id must not be null.") Long emitterAccountId) {
        this.emitterAccountId = emitterAccountId;
    }

    public @Null(message = "Receiver account id must be null for cash movement.", groups = PhysicalCashTransactionGroup.class) @Positive(message = "Receiver account id  must be positive.", groups = AdvancedGroup.class) @NotNull(message = "Receiver account id must not be null.", groups = DigitalTransactionGroup.class) Long getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(@Null(message = "Receiver account id must be null for cash movement.", groups = PhysicalCashTransactionGroup.class) @Positive(message = "Receiver account id  must be positive.", groups = AdvancedGroup.class) @NotNull(message = "Receiver account id must not be null.", groups = DigitalTransactionGroup.class) Long receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public @Min(value = 10, message = "Amount must be greater than 10 for cash movement.", groups = PhysicalCashTransactionGroup.class) @Min(value = 1, message = "Amount must be positive and greater than 0.", groups = DigitalTransactionGroup.class) @NotNull(message = "Amount must not be null.") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@Min(value = 10, message = "Amount must be greater than 10 for cash movement.", groups = PhysicalCashTransactionGroup.class) @Min(value = 1, message = "Amount must be positive and greater than 0.", groups = DigitalTransactionGroup.class) @NotNull(message = "Amount must not be null.") BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull(message = "Currency must not be null.") String getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull(message = "Currency must not be null.") String currency) {
        this.currency = currency;
    }

    public @NotNull(message = "Type must not be null.") TransactionType getType() {
        return type;
    }

    public void setType(@NotNull(message = "Type must not be null.") TransactionType type) {
        this.type = type;
    }

    public @NotNull(message = "Status must not be null.") TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Status must not be null.") TransactionStatus status) {
        this.status = status;
    }

    public @NotNull(message = "Date must not be null.") Instant getDate() {
        return date;
    }

    public void setDate(@NotNull(message = "Date must not be null.") Instant date) {
        this.date = date;
    }

    public @NotNull(message = "Label must not be null.") String getLabel() {
        return label;
    }

    public void setLabel(@NotNull(message = "Label must not be null.") String label) {
        this.label = label;
    }

    public @NotEmpty(message = "Bill must be define for cash movements.", groups = PhysicalCashTransactionGroup.class) @NotNull(message = "Metadata must not be null.") Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(@NotEmpty(message = "Bill must be define for cash movements.", groups = PhysicalCashTransactionGroup.class) @NotNull(message = "Metadata must not be null.") Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id)
                && Objects.equals(emitterAccountId, that.emitterAccountId)
                && Objects.equals(receiverAccountId, that.receiverAccountId)
                && Objects.equals(amount, that.amount)
                && Objects.equals(currency, that.currency)
                && type == that.type && status == that.status
                && Objects.equals(date, that.date)
                && Objects.equals(label, that.label)
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, emitterAccountId, receiverAccountId, amount, currency, type, status, date, label, metadata);
    }
}
