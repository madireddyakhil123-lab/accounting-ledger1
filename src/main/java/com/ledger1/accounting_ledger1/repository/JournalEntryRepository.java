package com.ledger1.accounting_ledger1.repository;



import com.ledger1.accounting_ledger1.entity.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface JournalEntryRepository
        extends JpaRepository<JournalEntry, Long> {

    Optional<JournalEntry> findByTenantIdAndIdempotencyKey(
            String tenantId,
            String idempotencyKey
    );

    Optional<JournalEntry> findByTenantIdAndId(
            String tenantId,
            Long id
    );

    Page<JournalEntry> findByTenantId(
            String tenantId,
            Pageable pageable
    );
    Page<JournalEntry> findByTenantIdAndEntryDateBetween(
            String tenantId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    boolean existsByTenantIdAndReversalOfId(
            String tenantId, Long reversalOfId);
}