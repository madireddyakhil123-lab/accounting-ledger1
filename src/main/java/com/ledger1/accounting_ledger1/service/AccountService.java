package com.ledger1.accounting_ledger1.service;

import com.ledger1.accounting_ledger1.entity.Account;
import com.ledger1.accounting_ledger1.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(
            String tenantId,
            String accountCode,
            String accountName) {

        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }

        if (accountCode == null || accountCode.isBlank()) {
            throw new IllegalArgumentException("Account code is required");
        }

        if (accountName == null || accountName.isBlank()) {
            throw new IllegalArgumentException("Account name is required");
        }

        if (accountRepository
                .findByTenantIdAndAccountCode(tenantId, accountCode)
                .isPresent()) {
            throw new IllegalArgumentException(
                    "Account code already exists for this tenant");
        }

        Account account = new Account();
        account.setTenantId(tenantId);
        account.setAccountCode(accountCode);
        account.setAccountName(accountName);

        return accountRepository.save(account);
    }
}