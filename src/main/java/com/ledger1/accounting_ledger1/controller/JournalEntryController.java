package com.ledger1.accounting_ledger1.controller;



import com.ledger1.accounting_ledger1.dto.JournalEntryRequest;
import com.ledger1.accounting_ledger1.dto.JournalEntryResponse;
import com.ledger1.accounting_ledger1.service.JournalEntryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/journal-entries")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    public JournalEntryController(
            JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JournalEntryResponse createEntry(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody JournalEntryRequest request) {

        return journalEntryService.createEntry(tenantId, request);
    }

    @GetMapping("/{id}")
    public JournalEntryResponse getEntry(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable Long id) {

        return journalEntryService.getEntryById(tenantId, id);
    }

    /*@GetMapping
    public Page<JournalEntryResponse> getAllEntries(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PageableDefault(size = 10, sort = "entryDate",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return journalEntryService.getAllEntries(tenantId, pageable);
    }*/

    @GetMapping
    public Page<JournalEntryResponse> getAllEntries(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 10, sort = "entryDate",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        return journalEntryService.getEntriesByDateRange(
                tenantId, startDate, endDate, pageable);
    }

    @PostMapping("/{id}/reverse")
    public JournalEntryResponse reverseEntry(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @PathVariable Long id) {

        return journalEntryService.reverseEntry(tenantId, id);
    }
}
