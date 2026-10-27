package com.ledger1.accounting_ledger1.repository;


import com.ledger1.accounting_ledger1.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository
        extends JpaRepository<Account, Long> {

    Optional<Account> findByTenantIdAndAccountCode(
            String tenantId,
            String accountCode
    );
}
