package com.hhp.ConcertReservation.application.scheduler;

import com.hhp.ConcertReservation.common.enums.SeatStatus;
import com.hhp.ConcertReservation.domain.entity.Reservation;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.domain.service.ReservationService;
import com.hhp.ConcertReservation.domain.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationExpiryScheduler {
	final ReservationService reservationService;
	final SeatService seatService;

	@Scheduled(fixedRate = 60000)
	public void processExpiredReservations() {
		List<Reservation> reservationsToExpire = reservationService.findReservationsToExpire(LocalDateTime.now());

		for (Reservation reservation : reservationsToExpire) {
			Seat seat = seatService.findSeatById(reservation.getSeatId());
			seat.setStatus(SeatStatus.AVAILABLE.name());
			seatService.save(seat);
		}
	}
}
