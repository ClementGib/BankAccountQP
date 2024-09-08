package com.cdx.bas.domain.bank.transaction;

import com.cdx.bas.domain.bank.transaction.type.TransactionType;

import java.math.BigDecimal;
import java.util.Map;

public record NewCashTransaction(Long emitterAccountId,
                                 BigDecimal amount,
                                 String currency,
                                 Map<String, String> metadata) {}
