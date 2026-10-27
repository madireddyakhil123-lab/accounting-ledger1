package com.ledger1.accounting_ledger1.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountBalanceResponse(
        String accountCode,
        String accountName,
        BigDecimal balance,
        LocalDate asOfDate
) {}