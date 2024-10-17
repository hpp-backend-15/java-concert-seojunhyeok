package com.hhp.ConcertReservation.application.dto;

import com.hhp.ConcertReservation.domain.model.Member;
import com.hhp.ConcertReservation.domain.model.Seat;

public class ReservationApplicationDto {
	public record reserveSeatResponse(
			Member member,
			Seat seat
	) { }
}
