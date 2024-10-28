package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.common.enums.ReservationStatus;
import com.hhp.ConcertReservation.domain.entity.Member;
import com.hhp.ConcertReservation.domain.entity.Reservation;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.infra.persistence.ReservationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class ReservationServiceTest {

	@Mock
	ReservationJpaRepository reservationJpaRepository;

	@InjectMocks
	ReservationService reservationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("예약 ID로 예약 조회 성공")
	void findById_success() {
		// Given
		Long reservationId = 1L;
		Reservation reservation = new Reservation();
		reservation.setId(reservationId);
		when(reservationJpaRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

		// When
		Reservation result = reservationService.findById(reservationId);

		// Then
		assertNotNull(result);
		assertEquals(reservationId, result.getId());
	}

	@Test
	@DisplayName("예약 ID로 예약 조회 실패 - 예외 발생")
	void findById_notFound() {
		// Given
		when(reservationJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// When & Then
		NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
			reservationService.findById(1L);
		});

		assertEquals("예약 정보를 찾을 수 없습니다. 예약 ID: 1", exception.getMessage());
	}

	@Test
	@DisplayName("예약 생성 성공")
	void createReservation_success() {
		// Given
		Member member = new Member();
		member.setId(1L);
		Seat seat = new Seat();
		seat.setId(1L);

		LocalDateTime now = LocalDateTime.now();
		Reservation reservation = new Reservation(member.getId(), seat.getId(), ReservationStatus.RESERVED.name(), now.plusMinutes(Reservation.SEAT_HOLD_TIME_MINUTES));

		when(reservationJpaRepository.save(any(Reservation.class))).thenReturn(reservation);

		// When
		Reservation result = reservationService.createReservation(member, seat);

		// Then
		assertNotNull(result);
		assertEquals(member.getId(), result.getMemberId());
		assertEquals(seat.getId(), result.getSeatId());
		assertEquals(ReservationStatus.RESERVED.name(), result.getStatus());
		assertTrue(result.getExpiryAt().isAfter(now));
		verify(reservationJpaRepository).save(any(Reservation.class));
	}
}
