package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.domain.entity.ConcertSchedule;
import com.hhp.ConcertReservation.infra.persistence.ConcertScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {
	final ConcertScheduleJpaRepository concertScheduleJpaRepository;

	public ConcertSchedule findConcertScheduleById(Long id) {
		return concertScheduleJpaRepository
				       .findById(id)
				       .orElseThrow(() -> new NoSuchElementException("콘서트 일정이 존재하지 않습니다."));
	}

	public List<ConcertSchedule> findAvailableConcertSchedules(LocalDateTime concert_date) {
		return concertScheduleJpaRepository.findByConcertDateGreaterThanEqual(concert_date);
	}
}
