package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.ReservationApplicationDto;
import com.hhp.ConcertReservation.domain.entity.Member;
import com.hhp.ConcertReservation.domain.entity.Reservation;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.domain.event.ReservationEventPublisher;
import com.hhp.ConcertReservation.domain.event.ReservationSuccessEvent;
import com.hhp.ConcertReservation.domain.service.MemberService;
import com.hhp.ConcertReservation.domain.service.ReservationService;
import com.hhp.ConcertReservation.domain.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationFacade {
	final MemberService memberService;
	final SeatService seatService;
	final ReservationService reservationService;
	final ReservationEventPublisher reservationEventPublisher;

	@Transactional
	@Retryable(
			retryFor = ObjectOptimisticLockingFailureException.class,
			noRetryFor = {IllegalStateException.class, NoSuchElementException.class, IllegalArgumentException.class},
			backoff = @Backoff(delay = 1000),
			notRecoverable = {IllegalStateException.class, NoSuchElementException.class, IllegalArgumentException.class})
	public ReservationApplicationDto.reserveSeatResponse reserveSeat(Long memberId, Long seatId) {
		Member member = memberService.findMemberById(memberId);

		Seat seat = seatService.reserveSeat(seatId);

		Reservation reservation = reservationService.createReservation(member, seat);

		// 관심사 분리 된 외부 로직 추가
		reservationEventPublisher.success(new ReservationSuccessEvent(reservation.getId().toString(), member.getId().toString()));

		return new ReservationApplicationDto.reserveSeatResponse(member, seat, reservation);
	}

	@Recover
	public ReservationApplicationDto.reserveSeatResponse recoverReserveSeat(ObjectOptimisticLockingFailureException e, Long memberId, Long seatId) {
		throw new IllegalStateException("재시도 횟수를 초과하여 좌석 예약에 실패했습니다." + memberId + seatId);
	}

}
