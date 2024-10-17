package com.hhp.ConcertReservation.application.dto;

import com.hhp.ConcertReservation.domain.model.Account;
import com.hhp.ConcertReservation.domain.model.AccountHistory;

public class AccountApplicationDto {
	public record chargeBalanceResponse(
			Account account,
			AccountHistory accountHistory
	) {}
}
