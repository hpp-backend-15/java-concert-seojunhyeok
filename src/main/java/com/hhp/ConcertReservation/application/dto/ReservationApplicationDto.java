package com.hhp.ConcertReservation.application.dto;

import com.hhp.ConcertReservation.domain.entity.Member;
import com.hhp.ConcertReservation.domain.entity.Seat;

public class ReservationApplicationDto {
	public record reserveSeatResponse(
			Member member,
			Seat seat
	) { }
}
