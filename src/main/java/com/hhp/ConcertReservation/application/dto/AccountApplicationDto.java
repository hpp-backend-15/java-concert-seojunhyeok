package com.hhp.ConcertReservation.application.dto;

import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.domain.entity.AccountHistory;

public class AccountApplicationDto {
	public record chargeBalanceResponse(
			Account account,
			AccountHistory history
	) {}
}
