package com.hhp.ConcertReservation.controller.scheduler;

import com.hhp.ConcertReservation.common.enums.ReservationStatus;
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
		//임시점유 기간이 종료된 예약 조회
		List<Reservation> reservationsToExpire = reservationService.findReservationsToExpire(LocalDateTime.now());

		for (Reservation reservation : reservationsToExpire) {
			//예약 상태 변경 RESERVED -> CANCELED
			reservation.setStatus(ReservationStatus.CANCELED.name());
			reservationService.save(reservation);

			//좌석 상태 변경 RESERVED -> AVAILABLE
			Seat seat = seatService.findSeatById(reservation.getSeatId());
			seat.setStatus(SeatStatus.AVAILABLE.name());
			seatService.save(seat);
		}
	}
}
