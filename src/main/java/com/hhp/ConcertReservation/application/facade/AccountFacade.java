package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.AccountApplicationDto;
import com.hhp.ConcertReservation.common.enums.AccountHistoryType;
import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.domain.entity.AccountHistory;
import com.hhp.ConcertReservation.domain.service.AccountHistoryService;
import com.hhp.ConcertReservation.domain.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountFacade {
	final AccountService accountService;
	final AccountHistoryService accountHistoryService;

	@Transactional
	public AccountApplicationDto.chargeBalanceResponse chargeBalance(Long memberId, Long amount) {
		Account account = accountService.findAccountByMemberIdWithLock(memberId);

		account.chargeBalance(amount);

		AccountHistory history = accountHistoryService.createHistory(account.getId(), amount, AccountHistoryType.CHARGE);

		return new AccountApplicationDto.chargeBalanceResponse(account, history);
	}
}
