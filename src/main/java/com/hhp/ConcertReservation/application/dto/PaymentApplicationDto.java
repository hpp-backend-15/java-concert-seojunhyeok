package com.hhp.ConcertReservation.application.dto;

import com.hhp.ConcertReservation.domain.entity.*;

public class PaymentApplicationDto {
	public record processReservationPaymentResponse(
			Reservation reservation,
			Seat seat,
			Account account,
			AccountHistory history,
			Queue queue
	) {}
}
