package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.ReservationApplicationDto;
import com.hhp.ConcertReservation.domain.entity.Member;
import com.hhp.ConcertReservation.domain.entity.Reservation;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.domain.service.MemberService;
import com.hhp.ConcertReservation.domain.service.ReservationService;
import com.hhp.ConcertReservation.domain.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationFacade {
	final MemberService memberService;
	final SeatService seatService;
	final ReservationService reservationService;

	@Transactional
	public ReservationApplicationDto.reserveSeatResponse reserveSeat(Long memberId, Long seatId) {
		Member member = memberService.findMemberById(memberId);

		Seat seat = seatService.reserveSeat(seatId);

		Reservation reservation = reservationService.createReservation(member, seat);

		return new ReservationApplicationDto.reserveSeatResponse(member, seat, reservation);
	}
}
