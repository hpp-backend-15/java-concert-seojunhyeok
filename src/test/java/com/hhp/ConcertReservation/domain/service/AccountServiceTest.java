package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.domain.model.Account;
import com.hhp.ConcertReservation.infra.persistence.AccountJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AccountServiceTest {

	@Mock
	AccountJpaRepository accountJpaRepository;

	@InjectMocks
	AccountService accountService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
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

		assertEquals("해당 멤버의 계좌를 찾을 수 없습니다. 계좌 ID: 1", exception.getMessage());
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
}
