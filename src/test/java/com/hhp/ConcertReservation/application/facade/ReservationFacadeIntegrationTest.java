package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.ReservationApplicationDto;
import com.hhp.ConcertReservation.common.enums.SeatStatus;
import com.hhp.ConcertReservation.domain.model.Member;
import com.hhp.ConcertReservation.domain.model.Seat;
import com.hhp.ConcertReservation.domain.service.MemberService;
import com.hhp.ConcertReservation.domain.service.ReservationService;
import com.hhp.ConcertReservation.domain.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class ReservationFacadeIntegrationTest {

	@Autowired
	private ReservationFacade reservationFacade;

	@Autowired
	private MemberService memberService;

	@Autowired
	private SeatService seatService;

	@Autowired
	private ReservationService reservationService;

	private Member member;
	private Seat availableSeat;
	private Seat reservedSeat;

	@BeforeEach
	void setUp() {
		member = new Member();
		member.setId(1L);
		member.setName("John Doe");

		availableSeat = new Seat();
		availableSeat.setId(1L);
		availableSeat.setConcertScheduleId(1L);
		availableSeat.setSeatNumber(1);
		availableSeat.setStatus(SeatStatus.AVAILABLE.toString());
		availableSeat.setPrice(100L);

		reservedSeat = new Seat();
		reservedSeat.setId(2L);
		reservedSeat.setConcertScheduleId(1L);
		reservedSeat.setSeatNumber(2);
		reservedSeat.setStatus(SeatStatus.RESERVED.toString());
		reservedSeat.setPrice(100L);

		when(memberService.findMemberById(1L)).thenReturn(member);
		when(seatService.findSeatById(1L)).thenReturn(availableSeat);
		when(seatService.findSeatById(2L)).thenReturn(reservedSeat);
	}

	@Test
	@DisplayName("성공적으로 좌석을 예약할 수 있다.")
	void reserveSeat_success() {
		ReservationApplicationDto.reserveSeatResponse response = reservationFacade.reserveSeat(member.getId(), availableSeat.getId());

		assertThat(response).isNotNull();
		assertThat(response.member()).isEqualTo(member);
		assertThat(response.seat()).isEqualTo(availableSeat);
		assertThat(availableSeat.getStatus()).isEqualTo(SeatStatus.RESERVED.toString());
		verify(reservationService, times(1)).createReservation(member, availableSeat);
	}

	@Test
	@DisplayName("존재하지 않는 멤버 ID로 좌석 예약을 시도하면 예외가 발생한다.")
	void reserveSeat_memberNotFound() {
		when(memberService.findMemberById(2L)).thenThrow(new IllegalArgumentException("멤버 정보를 찾을 수 없습니다."));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			reservationFacade.reserveSeat(2L, availableSeat.getId());
		});

		assertThat(exception.getMessage()).isEqualTo("멤버 정보를 찾을 수 없습니다.");
		verify(reservationService, never()).createReservation(any(), any());
	}

	@Test
	@DisplayName("존재하지 않는 좌석 ID로 좌석 예약을 시도하면 예외가 발생한다.")
	void reserveSeat_seatNotFound() {
		when(seatService.findSeatById(3L)).thenThrow(new IllegalArgumentException("좌석 정보를 찾을 수 없습니다."));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			reservationFacade.reserveSeat(member.getId(), 3L);
		});

		assertThat(exception.getMessage()).isEqualTo("좌석 정보를 찾을 수 없습니다.");
		verify(reservationService, never()).createReservation(any(), any());
	}

	@Test
	@DisplayName("이미 예약된 좌석으로 예약을 시도하면 예외가 발생한다.")
	void reserveSeat_seatAlreadyReserved() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			reservationFacade.reserveSeat(member.getId(), reservedSeat.getId());
		});

		assertThat(exception.getMessage()).isEqualTo("해당 좌석은 이미 예약되었습니다.");
		verify(reservationService, never()).createReservation(any(), any());
	}
}
