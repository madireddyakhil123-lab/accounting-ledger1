package com.ledger1.accounting_ledger1.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record JournalEntryRequest(
        @NotNull LocalDate entryDate,
        @Size(max = 500) String description,
        @NotBlank String idempotencyKey,
        @NotEmpty List<@Valid JournalEntryLineRequest> lines
) {}
