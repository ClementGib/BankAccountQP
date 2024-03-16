package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.bank.transaction.validation.validator.ValidStatus;
import com.cdx.bas.domain.bank.transaction.validation.validator.ValidType;
import com.cdx.bas.domain.bank.transaction.validation.group.*;
import com.cdx.bas.domain.bank.transaction.status.TransactionStatus;
import com.cdx.bas.domain.bank.transaction.type.TransactionType;
import com.cdx.bas.domain.currency.validation.ValidCurrency;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.cdx.bas.domain.bank.transaction.status.TransactionStatus.UNPROCESSED;
import static com.cdx.bas.domain.bank.transaction.type.TransactionType.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(emitterAccountId, that.emitterAccountId) &&
                Objects.equals(receiverAccountId, that.receiverAccountId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(type, that.type) &&
                Objects.equals(date, that.date) &&
                Objects.equals(label, that.label);
    }

    // Redéfinir hashCode() en excluant status et metadata
    @Override
    public int hashCode() {
        return Objects.hash(id, emitterAccountId, receiverAccountId, amount, currency, type, date, label);
    }
}
