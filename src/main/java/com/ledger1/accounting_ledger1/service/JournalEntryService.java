package com.ledger1.accounting_ledger1.service;



import com.ledger1.accounting_ledger1.dto.*;
import com.ledger1.accounting_ledger1.entity.*;
import com.ledger1.accounting_ledger1.exception.ResourceNotFoundException;
import com.ledger1.accounting_ledger1.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class JournalEntryService {

    private final AccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final JournalEntryLineRepository journalEntryLineRepository;

    public JournalEntryService(
            AccountRepository accountRepository,
            JournalEntryRepository journalEntryRepository, JournalEntryLineRepository journalEntryLineRepository) {
        this.accountRepository = accountRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.journalEntryLineRepository = journalEntryLineRepository;
    }

    @Transactional
    public JournalEntryResponse createEntry(
            String tenantId,
            JournalEntryRequest request) {

        var existing = journalEntryRepository
                .findByTenantIdAndIdempotencyKey(
                        tenantId, request.idempotencyKey());

        if (existing.isPresent()) {
            return toResponse(existing.get());
        }

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (JournalEntryLineRequest line : request.lines()) {
            if (line.debitAmount().signum() < 0
                    || line.creditAmount().signum() < 0) {
                throw new IllegalArgumentException(
                        "Amounts cannot be negative");
            }

            boolean debitPositive = line.debitAmount().signum() > 0;
            boolean creditPositive = line.creditAmount().signum() > 0;

            if (debitPositive == creditPositive) {
                throw new IllegalArgumentException(
                        "Each line must have either a debit or a credit");
            }

            totalDebit = totalDebit.add(line.debitAmount());
            totalCredit = totalCredit.add(line.creditAmount());
        }

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalArgumentException(
                    "Total debits must equal total credits");
        }

        JournalEntry entry = new JournalEntry();
        entry.setTenantId(tenantId);
        entry.setEntryDate(request.entryDate());
        entry.setDescription(request.description());
        entry.setIdempotencyKey(request.idempotencyKey());

        for (JournalEntryLineRequest lineRequest : request.lines()) {
            Account account = accountRepository
                    .findByTenantIdAndAccountCode(
                            tenantId, lineRequest.accountCode())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Account not found: " + lineRequest.accountCode()));

            JournalEntryLine line = new JournalEntryLine();
            line.setTenantId(tenantId);
            line.setAccount(account);
            line.setDebitAmount(lineRequest.debitAmount());
            line.setCreditAmount(lineRequest.creditAmount());

            entry.addLine(line);
        }

        JournalEntry saved = journalEntryRepository.save(entry);
        return toResponse(saved);
    }

    private JournalEntryResponse toResponse(JournalEntry entry) {
        var lines = new ArrayList<JournalEntryLineResponse>();

        for (JournalEntryLine line : entry.getLines()) {
            lines.add(new JournalEntryLineResponse(
                    line.getAccount().getAccountCode(),
                    line.getAccount().getAccountName(),
                    line.getDebitAmount(),
                    line.getCreditAmount()
            ));
        }

        return new JournalEntryResponse(
                entry.getId(),
                entry.getTenantId(),
                entry.getEntryDate(),
                entry.getDescription(),
                lines
        );
    }

    @Transactional(readOnly = true)
    public JournalEntryResponse getEntryById(String tenantId, Long id) {
        JournalEntry entry = journalEntryRepository
                .findByTenantIdAndId(tenantId, id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Journal entry not found"));

        return toResponse(entry);
    }

    @Transactional(readOnly = true)
    public Page<JournalEntryResponse> getAllEntries(
            String tenantId, Pageable pageable) {

        return journalEntryRepository.findByTenantId(tenantId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<JournalEntryResponse> getEntriesByDateRange(
            String tenantId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        if (startDate != null && endDate != null
                && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date must be before or equal to end date");
        }

        Page<JournalEntry> entries;

        if (startDate != null && endDate != null) {
            entries = journalEntryRepository
                    .findByTenantIdAndEntryDateBetween(
                            tenantId, startDate, endDate, pageable);
        } else {
            entries = journalEntryRepository.findByTenantId(tenantId, pageable);
        }

        return entries.map(this::toResponse);
    }

    @Transactional
    public JournalEntryResponse reverseEntry(String tenantId, Long id) {

        JournalEntry original = journalEntryRepository
                .findByTenantIdAndId(tenantId, id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Journal entry not found"));

        if (original.getReversalOfId() != null) {
            throw new IllegalArgumentException(
                    "A reversal entry cannot be reversed");
        }

        if (journalEntryRepository
                .existsByTenantIdAndReversalOfId(tenantId, id)) {
            throw new IllegalArgumentException(
                    "Journal entry has already been reversed");
        }

        JournalEntry reversal = new JournalEntry();
        reversal.setTenantId(tenantId);
        reversal.setEntryDate(LocalDate.now());
        reversal.setDescription("Reversal of journal entry " + id);
        reversal.setIdempotencyKey("reversal-" + id);
        reversal.setReversalOfId(id);

        for (JournalEntryLine originalLine : original.getLines()) {
            JournalEntryLine reversalLine = new JournalEntryLine();
            reversalLine.setTenantId(tenantId);
            reversalLine.setAccount(originalLine.getAccount());
            reversalLine.setDebitAmount(originalLine.getCreditAmount());
            reversalLine.setCreditAmount(originalLine.getDebitAmount());

            reversal.addLine(reversalLine);
        }

        return toResponse(journalEntryRepository.save(reversal));
    }

    @Transactional(readOnly = true)
    public AccountBalanceResponse getAccountBalance(
            String tenantId,
            String accountCode,
            LocalDate asOfDate) {

        Account account = accountRepository
                .findByTenantIdAndAccountCode(tenantId, accountCode)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found"));

        BigDecimal balance = journalEntryLineRepository.calculateBalance(
                tenantId, accountCode, asOfDate);

        return new AccountBalanceResponse(
                account.getAccountCode(),
                account.getAccountName(),
                balance,
                asOfDate
        );
    }
}
