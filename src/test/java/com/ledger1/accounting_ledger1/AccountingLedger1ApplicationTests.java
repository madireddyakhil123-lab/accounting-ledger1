
package com.ledger1.accounting_ledger1;

import com.ledger1.accounting_ledger1.dto.JournalEntryLineRequest;
import com.ledger1.accounting_ledger1.dto.JournalEntryRequest;
import com.ledger1.accounting_ledger1.repository.AccountRepository;
import com.ledger1.accounting_ledger1.repository.JournalEntryLineRepository;
import com.ledger1.accounting_ledger1.repository.JournalEntryRepository;
import com.ledger1.accounting_ledger1.service.JournalEntryService;
import com.ledger1.accounting_ledger1.entity.JournalEntry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AccountingLedger1ApplicationTests {

	private JournalEntryService service;
	private JournalEntryRepository entryRepository;
	private AccountRepository accountRepository;
	private JournalEntryLineRepository lineRepository;

	@BeforeEach
	void setUp() {
		entryRepository = mock(JournalEntryRepository.class);
		accountRepository = mock(AccountRepository.class);
		lineRepository = mock(JournalEntryLineRepository.class);

		service = new JournalEntryService(
				accountRepository, entryRepository, lineRepository);
	}

	@Test
	void unbalancedEntryShouldBeRejected() {
		JournalEntryRequest request = new JournalEntryRequest(
				LocalDate.now(),
				"Unbalanced test",
				"test-key-001",
				List.of(
						new JournalEntryLineRequest(
								"BANK-001",
								new BigDecimal("100"),
								BigDecimal.ZERO),
						new JournalEntryLineRequest(
								"SALES-001",
								BigDecimal.ZERO,
								new BigDecimal("80"))
				)
		);

		when(entryRepository.findByTenantIdAndIdempotencyKey(
				"acme-corp", "test-key-001"))
				.thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class,
				() -> service.createEntry("acme-corp", request));

		verify(entryRepository, never()).save(any());
	}

	@Test
	void duplicateIdempotencyKeyShouldReturnExistingEntry() {
		JournalEntry existing = new JournalEntry();
		existing.setTenantId("acme-corp");
		existing.setEntryDate(LocalDate.of(2026, 10, 25));
		existing.setDescription("Existing payment");
		existing.setIdempotencyKey("payment-1001");

		when(entryRepository.findByTenantIdAndIdempotencyKey(
				"acme-corp", "payment-1001"))
				.thenReturn(Optional.of(existing));

		JournalEntryRequest request = new JournalEntryRequest(
				LocalDate.of(2026, 10, 25),
				"Existing payment",
				"payment-1001",
				List.of()
		);

		service.createEntry("acme-corp", request);

		verify(entryRepository, times(1))
				.findByTenantIdAndIdempotencyKey(
						"acme-corp", "payment-1001");

		verify(entryRepository, never()).save(any());
	}


	@Test
	void tenantCannotAccessAnotherTenantsEntry() {
		when(entryRepository.findByTenantIdAndId("tenant-B", 100L))
				.thenReturn(Optional.empty());

		assertThrows(
				com.ledger1.accounting_ledger1.exception.ResourceNotFoundException.class,
				() -> service.getEntryById("tenant-B", 100L)
		);

		verify(entryRepository).findByTenantIdAndId("tenant-B", 100L);
	}


}
