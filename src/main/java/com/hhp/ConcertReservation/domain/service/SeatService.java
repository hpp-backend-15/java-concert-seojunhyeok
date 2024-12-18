package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.common.enums.SeatStatus;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.infra.persistence.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SeatService {
	final SeatJpaRepository seatJpaRepository;

	public Seat save(Seat seat) {
		return seatJpaRepository.save(seat);
	}

	public Seat findSeatById(Long seatId) {
		return seatJpaRepository.findById(seatId)
				       .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다."));
	}

	public Seat findSeatByIdWithLock(Long seatId) {
		return seatJpaRepository.findByIdWithLock(seatId)
				       .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다."));
	}

	public List<Seat> findAvailableSeats(Long concertScheduleId) {
		return seatJpaRepository.findByConcertScheduleIdAndStatus(concertScheduleId, SeatStatus.AVAILABLE.name());
	}

	@Transactional
	public Seat reserveSeat(Long seatId) {
		Seat seat = seatJpaRepository
				            .findByIdWithLock(seatId)
				            .orElseThrow(() -> new NoSuchElementException("좌석 정보를 찾을 수 없습니다."));
		seat.validateSeatAvailability();
		seat.setStatus(SeatStatus.RESERVED.name());
		return seatJpaRepository.save(seat);
	}
}
