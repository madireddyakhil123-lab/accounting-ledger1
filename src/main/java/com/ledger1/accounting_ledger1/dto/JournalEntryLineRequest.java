package com.ledger1.accounting_ledger1.dto;



import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record JournalEntryLineRequest(
        @NotBlank String accountCode,
        @NotNull @DecimalMin("0.00") BigDecimal debitAmount,
        @NotNull @DecimalMin("0.00") BigDecimal creditAmount
) {}
