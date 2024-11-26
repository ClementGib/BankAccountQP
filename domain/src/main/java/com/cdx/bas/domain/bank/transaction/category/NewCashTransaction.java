package com.cdx.bas.domain.bank.transaction.category;

import java.math.BigDecimal;
import java.util.Map;

public record NewCashTransaction(Long emitterAccountId,
                                 BigDecimal amount,
                                 String currency,
                                 Map<String, String> metadata) {}
