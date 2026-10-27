package com.ledger1.accounting_ledger1.dto;



import java.math.BigDecimal;

public record JournalEntryLineResponse(
        String accountCode,
        String accountName,
        BigDecimal debitAmount,
        BigDecimal creditAmount
) {}
