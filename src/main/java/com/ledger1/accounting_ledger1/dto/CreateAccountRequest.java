package com.ledger1.accounting_ledger1.dto;

public record CreateAccountRequest(
        String accountCode,
        String accountName
) {}