package com.ledger1.accounting_ledger1.repository;

import com.ledger1.accounting_ledger1.entity.JournalEntryLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface JournalEntryLineRepository
        extends JpaRepository<JournalEntryLine, Long> {

    @Query("""
        SELECT COALESCE(SUM(l.debitAmount - l.creditAmount), 0)
        FROM JournalEntryLine l
        JOIN l.journalEntry j
        JOIN l.account a
        WHERE l.tenantId = :tenantId
          AND a.accountCode = :accountCode
          AND j.tenantId = :tenantId
          AND (:asOfDate IS NULL OR j.entryDate <= :asOfDate)
        """)
    BigDecimal calculateBalance(
            @Param("tenantId") String tenantId,
            @Param("accountCode") String accountCode,
            @Param("asOfDate") LocalDate asOfDate
    );
}