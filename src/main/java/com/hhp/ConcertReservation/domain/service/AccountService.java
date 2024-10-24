package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.infra.persistence.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountService {
	final AccountJpaRepository accountJpaRepository;

	public Account findAccountById(Long accountId) {
		return accountJpaRepository
				       .findById(accountId)
				       .orElseThrow(() -> new NoSuchElementException("해당 계좌를 찾을 수 없습니다. 계좌 ID: " + accountId));
	}

	public Account findAccountByMemberId(Long memberId) {
		return accountJpaRepository
				       .findByMemberId(memberId)
				       .orElseThrow(() -> new NoSuchElementException("해당 멤버의 계좌를 찾을 수 없습니다. 멤버 ID: " + memberId));
	}

	public void chargeBalance(Long accountId, Long amount) {
		Account account = findAccountById(accountId);
		account.chargeBalance(amount);
		accountJpaRepository.save(account);
	}

	public Account save(Account account) {
		return accountJpaRepository.save(account);
	}
}
