package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.infra.persistence.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
	final AccountJpaRepository accountJpaRepository;

	public Account findAccountById(Long accountId) {
		return accountJpaRepository
				       .findById(accountId)
				       .orElseThrow(() -> new NoSuchElementException("해당 계좌를 찾을 수 없습니다. 계좌 ID: " + accountId));
	}

	public Account findAccountByIdWithLock(Long accountId) {
		return accountJpaRepository
				       .findByIdWithLock(accountId)
				       .orElseThrow(() -> new NoSuchElementException("해당 계좌를 찾을 수 없습니다. 계좌 ID: " + accountId));
	}

	public Account findAccountByMemberId(Long memberId) {
		return accountJpaRepository
				       .findByMemberId(memberId)
				       .orElseThrow(() -> new NoSuchElementException("해당 멤버의 계좌를 찾을 수 없습니다. 멤버 ID: " + memberId));
	}

	public Account findAccountByMemberIdWithLock(Long memberId) {
		return accountJpaRepository
				       .findByMemberIdWithLock(memberId)
				       .orElseThrow(() -> new NoSuchElementException("해당 멤버의 계좌를 찾을 수 없습니다. 멤버 ID: " + memberId));
	}

	@Transactional
	public Account chargeBalance(Long accountId, Long amount) {
		Account account = accountJpaRepository
				                  .findById(accountId)
				                  .orElseThrow(() -> new NoSuchElementException("해당 계좌를 찾을 수 없습니다. 계좌 ID: " + accountId));

		account.chargeBalance(amount);
		Account save = accountJpaRepository.save(account);
		return save;
	}

	@Transactional
	public Account useBalance(Long accountId, Long amount) {
		Account account = accountJpaRepository
				                  .findById(accountId)
				                  .orElseThrow(() -> new NoSuchElementException("해당 계좌를 찾을 수 없습니다. 계좌 ID: " + accountId));

		account.useBalance(amount);
		Account save = accountJpaRepository.save(account);
		return save;
	}

	public Account save(Account account) {
		return accountJpaRepository.save(account);
	}
}
