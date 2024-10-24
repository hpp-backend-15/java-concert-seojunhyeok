package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.common.enums.SeatStatus;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.infra.persistence.SeatJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class SeatServiceTest {

	@Mock
	SeatJpaRepository seatJpaRepository;

	@InjectMocks
	SeatService seatService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("좌석 ID로 좌석 조회 성공")
	void findSeatById_success() {
		// Given
		Long seatId = 1L;
		Seat seat = new Seat();
		seat.setId(seatId);
		when(seatJpaRepository.findById(seatId)).thenReturn(Optional.of(seat));

		// When
		Seat result = seatService.findSeatById(seatId);

		// Then
		assertNotNull(result);
		assertEquals(seatId, result.getId());
	}

	@Test
	@DisplayName("좌석 ID로 좌석 조회 실패 - 예외 발생")
	void findSeatById_notFound() {
		// Given
		when(seatJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// When & Then
		NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
			seatService.findSeatById(1L);
		});

		assertEquals("좌석 정보를 찾을 수 없습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("예약 가능한 좌석 조회 성공")
	void findAvailableSeats_success() {
		// Given
		Long concertScheduleId = 1L;
		Seat seat1 = new Seat();
		seat1.setId(1L);
		seat1.setStatus(SeatStatus.AVAILABLE.name());
		Seat seat2 = new Seat();
		seat2.setId(2L);
		seat2.setStatus(SeatStatus.AVAILABLE.name());

		when(seatJpaRepository.findByConcertScheduleIdAndStatus(concertScheduleId, SeatStatus.AVAILABLE.name()))
				.thenReturn(List.of(seat1, seat2));

		// When
		List<Seat> result = seatService.findAvailableSeats(concertScheduleId);

		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(SeatStatus.AVAILABLE.name(), result.get(0).getStatus());
		assertEquals(SeatStatus.AVAILABLE.name(), result.get(1).getStatus());
	}

	@Test
	@DisplayName("예약 가능한 좌석이 없을 때 빈 리스트를 반환해야 한다")
	void findAvailableSeats_noSeats() {
		// Given
		Long concertScheduleId = 1L;
		when(seatJpaRepository.findByConcertScheduleIdAndStatus(concertScheduleId, SeatStatus.AVAILABLE.name()))
				.thenReturn(List.of());

		// When
		List<Seat> result = seatService.findAvailableSeats(concertScheduleId);

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}
