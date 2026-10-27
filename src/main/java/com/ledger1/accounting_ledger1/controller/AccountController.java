package com.ledger1.accounting_ledger1.controller;

import com.ledger1.accounting_ledger1.dto.AccountBalanceResponse;
import com.ledger1.accounting_ledger1.dto.CreateAccountRequest;
import com.ledger1.accounting_ledger1.entity.Account;
import com.ledger1.accounting_ledger1.service.AccountService;
import com.ledger1.accounting_ledger1.service.JournalEntryService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final JournalEntryService journalEntryService;
    private final AccountService accountService;

    public AccountController(
            JournalEntryService journalEntryService,
            AccountService accountService) {
        this.journalEntryService = journalEntryService;
        this.accountService = accountService;
    }

    @GetMapping("/{accountCode}/balance")
    public AccountBalanceResponse getAccountBalance(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable String accountCode,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate asOfDate) {

        return journalEntryService.getAccountBalance(
                tenantId, accountCode, asOfDate);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestBody CreateAccountRequest request) {

        return accountService.createAccount(
                tenantId,
                request.accountCode(),
                request.accountName());
    }
}