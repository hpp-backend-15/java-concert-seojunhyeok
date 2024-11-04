package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.AccountApplicationDto;
import com.hhp.ConcertReservation.common.enums.AccountHistoryType;
import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.domain.entity.AccountHistory;
import com.hhp.ConcertReservation.domain.service.AccountHistoryService;
import com.hhp.ConcertReservation.domain.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountFacade {
	final AccountService accountService;
	final AccountHistoryService accountHistoryService;

	@Transactional
	@Retryable(retryFor = ObjectOptimisticLockingFailureException.class
			, backoff = @Backoff(delay = 1000)
			, recover = "recoverChargeBalance"
			, noRetryFor = {IllegalStateException.class, NoSuchElementException.class, IllegalArgumentException.class}
			, notRecoverable = {IllegalStateException.class, NoSuchElementException.class, IllegalArgumentException.class})
	public AccountApplicationDto.chargeBalanceResponse chargeBalance(Long accountId, Long amount) {
		Account account = accountService.chargeBalance(accountId, amount);

		AccountHistory history = accountHistoryService.createHistory(account.getId(), amount, AccountHistoryType.CHARGE);

		return new AccountApplicationDto.chargeBalanceResponse(account, history);
	}

	@Recover
	public AccountApplicationDto.chargeBalanceResponse recoverChargeBalance(ObjectOptimisticLockingFailureException e, Long accountId, Long amount) {
		throw new IllegalStateException("재시도 횟수를 초과하여 계좌 충전에 실패했습니다. 계좌 ID: " + accountId + ", 충전 금액: " + amount, e);
	}
}
