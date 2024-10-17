package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.common.enums.AccountHistoryType;
import com.hhp.ConcertReservation.domain.model.AccountHistory;
import com.hhp.ConcertReservation.infra.persistence.AccountHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountHistoryService {
	final AccountHistoryJpaRepository accountHistoryJpaRepository;

	public AccountHistory createHistory(Long accountId, Long amount, AccountHistoryType type) {
		AccountHistory accountHistory = new AccountHistory();
		accountHistory.setAccountId(accountId);
		accountHistory.setAmount(amount);
		accountHistory.setType(type);
		accountHistory.setCreateAt(LocalDateTime.now());

		accountHistoryJpaRepository.save(accountHistory);

		return accountHistory;
	}
}
