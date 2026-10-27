package com.ledger1.accounting_ledger1.dto;


import java.time.LocalDate;
import java.util.List;

public record JournalEntryResponse(
        Long id,
        String tenantId,
        LocalDate entryDate,
        String description,
        List<JournalEntryLineResponse> lines
) {}
