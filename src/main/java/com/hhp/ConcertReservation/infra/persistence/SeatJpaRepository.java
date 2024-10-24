package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
	List<Seat> findByConcertScheduleIdAndStatus(Long concertScheduleId, String status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Seat s where s.id = :seatId")
	Optional<Seat> findByIdWithLock(Long seatId);
}
