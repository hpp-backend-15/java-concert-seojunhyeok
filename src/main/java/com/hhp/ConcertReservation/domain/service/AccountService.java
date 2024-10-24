package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.domain.model.Account;
import com.hhp.ConcertReservation.infra.persistence.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
	final AccountJpaRepository accountJpaRepository;

	public Account findAccountById(Long accountId) {
		return accountJpaRepository
				       .findById(accountId)
				       .orElseThrow(() -> new IllegalArgumentException("해당 멤버의 계좌를 찾을 수 없습니다. 계좌 ID: " + accountId));
	}

	public Account findAccountByMemberId(Long memberId) {
		return accountJpaRepository
				       .findByMemberId(memberId)
				       .orElseThrow(() -> new IllegalArgumentException("해당 멤버의 계좌를 찾을 수 없습니다. 멤버 ID: " + memberId));
	}

	public Account createAccount(Account account) {
		return accountJpaRepository.save(account);
	}

	public Account updateAccount(Account account) {
		return accountJpaRepository.save(account);
	}
}
