package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
	List<Seat> findByConcertScheduleIdAndStatus(Long concertScheduleId, String status);
}
