package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.ReservationApplicationDto;
import com.hhp.ConcertReservation.common.enums.SeatStatus;
import com.hhp.ConcertReservation.domain.entity.Member;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.domain.event.ReservationEventListener;
import com.hhp.ConcertReservation.domain.service.MemberService;
import com.hhp.ConcertReservation.domain.service.SeatService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase
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
	private ReservationEventListener reservationEventListener;

	private Member member;
	private Seat availableSeat;
	private Seat reservedSeat;

	@BeforeEach
	void setUp() {
		member = new Member();
		member.setName("John Doe");

		availableSeat = new Seat();
		availableSeat.setConcertScheduleId(1L);
		availableSeat.setSeatNumber(1);
		availableSeat.setStatus(SeatStatus.AVAILABLE.name());
		availableSeat.setPrice(100L);

		reservedSeat = new Seat();
		reservedSeat.setConcertScheduleId(1L);
		reservedSeat.setSeatNumber(2);
		reservedSeat.setStatus(SeatStatus.RESERVED.name());
		reservedSeat.setPrice(100L);
	}

	@Test
	@DisplayName("성공적으로 좌석을 예약할 수 있다.")
	void reserveSeat_success() {
		//given
		Member savedMember = memberService.save(member);
		Seat savedAvailableSeat = seatService.save(availableSeat);

		//when
		ReservationApplicationDto.reserveSeatResponse response = reservationFacade.reserveSeat(savedMember.getId(), savedAvailableSeat.getId());

		//then
		assertThat(response).isNotNull();
		assertThat(response.member()).isEqualTo(savedMember);
		assertThat(response.seat().getId()).isEqualTo(savedAvailableSeat.getId());
		assertThat(response.seat().getStatus()).isEqualTo(SeatStatus.RESERVED.name());
		System.out.println(reservationEventListener.isEventReceived());
		assertThat(reservationEventListener.isEventReceived()).isTrue();
	}

	@Test
	@DisplayName("존재하지 않는 멤버 ID로 좌석 예약을 시도하면 예외가 발생한다.")
	void reserveSeat_memberNotFound() {
		//given
		Long memberId = 2L;
		Seat savedAvailableSeat = seatService.save(availableSeat);

		//when & then
		assertThrows(NoSuchElementException.class, () -> {
			reservationFacade.reserveSeat(memberId, savedAvailableSeat.getId());
		});
	}

	@Test
	@DisplayName("존재하지 않는 좌석 ID로 좌석 예약을 시도하면 예외가 발생한다.")
	void reserveSeat_seatNotFound() {
		//given
		Member savedMember = memberService.save(member);
		Long seatId = 1L;

		//when & then
		assertThrows(NoSuchElementException.class, () -> {
			reservationFacade.reserveSeat(savedMember.getId(), seatId);
		});
	}

	@Test
	@DisplayName("이미 예약된 좌석으로 예약을 시도하면 예외가 발생한다.")
	void reserveSeat_seatAlreadyReserved() {
		//given
		Member savedMember = memberService.save(member);
		Seat savedSeat = seatService.save(reservedSeat);

		//when
		assertThrows(IllegalStateException.class, () -> {
			reservationFacade.reserveSeat(savedMember.getId(), savedSeat.getId());
		});
	}
}
