package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.AccountApplicationDto;
import com.hhp.ConcertReservation.common.enums.AccountHistoryType;
import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.domain.entity.AccountHistory;
import com.hhp.ConcertReservation.domain.service.AccountHistoryService;
import com.hhp.ConcertReservation.domain.service.AccountService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase
@SpringBootTest
class AccountFacadeIntegrationTest {

	@Autowired
	private AccountFacade accountFacade;

	@Autowired
	private AccountService accountService;

	private Account account;
	@Autowired
	private AccountHistoryService accountHistoryService;

	@BeforeEach
	void setUp() {
		account = new Account();
		account.setMemberId(1L);
		account.setBalance(1000L);
		account = accountService.save(account);
	}

	@Test
	@Transactional
	@DisplayName("충전 성공 - Account와 AccountHistory가 업데이트 된다")
	void chargeBalance_Success() {
		// Given
		Long amount = 500L;

		// When
		AccountApplicationDto.chargeBalanceResponse response = accountFacade.chargeBalance(account.getId(), amount);

		// Then
		Account updatedAccount = response.account();
		AccountHistory accountHistory = response.history();

		// Account 검증
		assertNotNull(updatedAccount);
		assertEquals(1500L, updatedAccount.getBalance());  // 1000L + 500L = 1500L

		// AccountHistory 검증
		assertNotNull(accountHistory);
		assertEquals(account.getId(), accountHistory.getAccountId());
		assertEquals(amount, accountHistory.getAmount());
		assertEquals(AccountHistoryType.CHARGE.name(), accountHistory.getType());
	}

	@Test
	@Transactional
	@DisplayName("충전 실패 - 잘못된 금액 입력시 예외 발생")
	void chargeBalance_Fail_InvalidAmount() {
		// Given
		Long amount = -100L;

		// When & Then
		assertThrows(IllegalArgumentException.class, () -> accountFacade.chargeBalance(account.getMemberId(), amount));
		var histories = accountHistoryService.findAllByAccountId(account.getId());
		assertThat(histories).isEmpty();
	}
}
