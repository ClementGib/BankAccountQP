package com.cdx.bas.domain.bank.transaction.category;

import com.cdx.bas.domain.bank.transaction.category.digital.type.TransactionType;

import java.math.BigDecimal;
import java.util.Map;


public record NewDigitalTransaction(Long emitterAccountId, Long receiverAccountId,
                                    BigDecimal amount, String currency,
                                    TransactionType type, String label,
                                    Map<String, String> metadata) {}
