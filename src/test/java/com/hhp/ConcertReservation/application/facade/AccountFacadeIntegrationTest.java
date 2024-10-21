package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.AccountApplicationDto;
import com.hhp.ConcertReservation.common.enums.AccountHistoryType;
import com.hhp.ConcertReservation.domain.model.Account;
import com.hhp.ConcertReservation.domain.model.AccountHistory;
import com.hhp.ConcertReservation.domain.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountFacadeIntegrationTest {

	@Autowired
	private AccountFacade accountFacade;

	@Autowired
	private AccountService accountService;

	private Long accountId;

	@BeforeEach
	void setUp() {
		// 필요한 데이터 미리 설정
		Account account = new Account();
		account.setMemberId(1L);
		account.setBalance(1000L);
		accountService.createAccount(account);
		this.accountId = account.getId();
	}

	@Test
	@DisplayName("충전 성공 - Account와 AccountHistory가 업데이트 된다")
	void chargeBalance_Success() {
		// Given
		Long amount = 500L;

		// When
		AccountApplicationDto.chargeBalanceResponse response = accountFacade.chargeBalance(accountId, amount);

		// Then
		Account updatedAccount = response.account();
		AccountHistory accountHistory = response.accountHistory();

		// Account 검증
		assertNotNull(updatedAccount);
		assertEquals(1500L, updatedAccount.getBalance());  // 1000L + 500L = 1500L

		// AccountHistory 검증
		assertNotNull(accountHistory);
		assertEquals(accountId, accountHistory.getAccountId());
		assertEquals(amount, accountHistory.getAmount());
		assertEquals(AccountHistoryType.CHARGE, accountHistory.getType());
	}

	@Test
	@DisplayName("충전 실패 - 잘못된 금액 입력시 예외 발생")
	void chargeBalance_Fail_InvalidAmount() {
		// Given
		Long amount = -100L;

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			accountFacade.chargeBalance(accountId, amount);
		});

		assertEquals("충전금액은 0보다 커야합니다.", exception.getMessage());
	}
}
