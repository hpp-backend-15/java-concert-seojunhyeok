package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.infra.persistence.AccountJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AccountServiceTest {

	@Mock
	AccountJpaRepository accountJpaRepository;

	@InjectMocks
	AccountService accountService;

	private Account account;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		account = new Account();
		account.setId(1L);
		account.setMemberId(1L);
		account.setBalance(500L);
	}

	@Test
	@DisplayName("계좌 ID로 계좌 조회 성공")
	void findAccountById_success() {
		// Given
		Account account = new Account();
		account.setId(1L);
		when(accountJpaRepository.findById(1L)).thenReturn(Optional.of(account));

		// When
		Account result = accountService.findAccountById(1L);

		// Then
		assertNotNull(result);
		assertEquals(1L, result.getId());
	}

	@Test
	@DisplayName("계좌 ID로 계좌 조회 실패 - 예외 발생")
	void findAccountById_notFound() {
		// Given
		when(accountJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			accountService.findAccountById(1L);
		});

		assertEquals("해당 계좌를 찾을 수 없습니다. 계좌 ID: 1", exception.getMessage());
	}

	@Test
	@DisplayName("멤버 ID로 계좌 조회 성공")
	void findAccountByMemberId_success() {
		// Given
		Account account = new Account();
		account.setMemberId(1L);
		when(accountJpaRepository.findByMemberId(1L)).thenReturn(Optional.of(account));

		// When
		Account result = accountService.findAccountByMemberId(1L);

		// Then
		assertNotNull(result);
		assertEquals(1L, result.getMemberId());
	}

	@Test
	@DisplayName("멤버 ID로 계좌 조회 실패 - 예외 발생")
	void findAccountByMemberId_notFound() {
		// Given
		when(accountJpaRepository.findByMemberId(anyLong())).thenReturn(Optional.empty());

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			accountService.findAccountByMemberId(1L);
		});

		assertEquals("해당 멤버의 계좌를 찾을 수 없습니다. 멤버 ID: 1", exception.getMessage());
	}

	@Test
	@DisplayName("성공적으로 계좌에 잔액을 충전할 수 있다.")
	void chargeBalance_success() {
		// Given
		Long accountId = 1L;
		Long chargeAmount = 100L;

		when(accountJpaRepository.findById(accountId)).thenReturn(java.util.Optional.of(account));

		// When
		accountService.chargeBalance(accountId, chargeAmount);

		// Then
		verify(accountJpaRepository, times(1)).findById(accountId);
		verify(accountJpaRepository, times(1)).save(account);
		assertThat(account.getBalance()).isEqualTo(600L);  // 충전 후 잔액 확인
	}

	@Test
	@DisplayName("존재하지 않는 계좌 ID로 잔액을 충전하려고 하면 예외가 발생한다.")
	void chargeBalance_accountNotFound() {
		// Given
		Long invalidAccountId = 999L;
		Long chargeAmount = 100L;

		when(accountJpaRepository.findById(invalidAccountId)).thenReturn(java.util.Optional.empty());

		// When & Then
		assertThrows(IllegalArgumentException.class, () -> {
			accountService.chargeBalance(invalidAccountId, chargeAmount);
		});

		verify(accountJpaRepository, times(1)).findById(invalidAccountId);
		verify(accountJpaRepository, never()).save(any());
	}

	@Test
	@DisplayName("충전 금액이 0 이하일 경우 예외가 발생한다.")
	void chargeBalance_invalidAmount() {
		// Given
		Long accountId = 1L;
		Long invalidChargeAmount = 0L;

		when(accountJpaRepository.findById(accountId)).thenReturn(java.util.Optional.of(account));

		// When & Then
		assertThrows(IllegalArgumentException.class, () -> {
			accountService.chargeBalance(accountId, invalidChargeAmount);
		});

		verify(accountJpaRepository, times(1)).findById(accountId);
		verify(accountJpaRepository, never()).save(any());
	}
}
