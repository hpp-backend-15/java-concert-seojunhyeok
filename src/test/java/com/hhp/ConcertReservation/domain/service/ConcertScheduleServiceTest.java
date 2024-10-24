package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.domain.entity.ConcertSchedule;
import com.hhp.ConcertReservation.infra.persistence.ConcertScheduleJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

class ConcertScheduleServiceTest {

	@Mock
	ConcertScheduleJpaRepository concertScheduleJpaRepository;

	@InjectMocks
	ConcertScheduleService concertScheduleService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("콘서트 일정 ID로 조회 성공")
	void findConcertScheduleById_success() {
		// Given
		ConcertSchedule concertSchedule = new ConcertSchedule();
		concertSchedule.setId(1L);
		when(concertScheduleJpaRepository.findById(1L)).thenReturn(Optional.of(concertSchedule));

		// When
		ConcertSchedule result = concertScheduleService.findConcertScheduleById(1L);

		// Then
		assertNotNull(result);
		assertEquals(1L, result.getId());
	}

	@Test
	@DisplayName("콘서트 일정 ID로 조회 실패 - 예외 발생")
	void findConcertScheduleById_notFound() {
		// Given
		when(concertScheduleJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// When & Then
		NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
			concertScheduleService.findConcertScheduleById(1L);
		});

		assertEquals("콘서트 일정이 존재하지 않습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("콘서트 날짜 이후의 예약 가능한 일정 조회 성공")
	void findAvailableConcertSchedules_success() {
		// Given
		LocalDateTime concertDate = LocalDateTime.now();
		ConcertSchedule concertSchedule1 = new ConcertSchedule();
		concertSchedule1.setId(1L);
		ConcertSchedule concertSchedule2 = new ConcertSchedule();
		concertSchedule2.setId(2L);

		when(concertScheduleJpaRepository.findByConcertDateGreaterThanEqual(concertDate))
				.thenReturn(Arrays.asList(concertSchedule1, concertSchedule2));

		// When
		List<ConcertSchedule> result = concertScheduleService.findAvailableConcertSchedules(concertDate);

		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(1L, result.get(0).getId());
		assertEquals(2L, result.get(1).getId());
	}

	@Test
	@DisplayName("예약 가능한 콘서트 일정이 없는 경우")
	void findAvailableConcertSchedules_noSchedules() {
		// Given
		LocalDateTime concertDate = LocalDateTime.now();
		when(concertScheduleJpaRepository.findByConcertDateGreaterThanEqual(concertDate))
				.thenReturn(List.of());

		// When
		List<ConcertSchedule> result = concertScheduleService.findAvailableConcertSchedules(concertDate);

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}
