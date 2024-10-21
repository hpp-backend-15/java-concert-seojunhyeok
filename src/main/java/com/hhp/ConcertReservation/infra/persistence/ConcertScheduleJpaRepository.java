package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.model.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule, Long> {
	List<ConcertSchedule> findByConcertDateGreaterThanEqual(LocalDateTime date);
}
