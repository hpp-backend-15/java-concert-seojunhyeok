package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.common.enums.ReservationStatus;
import com.hhp.ConcertReservation.domain.entity.Member;
import com.hhp.ConcertReservation.domain.entity.Reservation;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.infra.persistence.ReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationJpaRepository reservationJpaRepository;

	public Reservation save(Reservation reservation) {
		return reservationJpaRepository.save(reservation);
	}

	public Reservation findById(Long reservationId) {
		return reservationJpaRepository
				       .findById(reservationId)
				       .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다. 예약 ID: " + reservationId));
	}

	public Reservation createReservation(Member member, Seat seat) {
		LocalDateTime createAt = LocalDateTime.now();
		LocalDateTime expiryAt = createAt.plusMinutes(Reservation.SEAT_HOLD_TIME_MINUTES);

		Reservation reservation = new Reservation(member.getId(), seat.getId(), ReservationStatus.RESERVED.name(), expiryAt);

		reservationJpaRepository.save(reservation);
		return reservation;
	}

	public List<Reservation> findReservationsToExpire(LocalDateTime expiryAt) {
		return reservationJpaRepository.findByStatusAndExpiryAtBefore(ReservationStatus.RESERVED.name(), expiryAt);
	}
}
