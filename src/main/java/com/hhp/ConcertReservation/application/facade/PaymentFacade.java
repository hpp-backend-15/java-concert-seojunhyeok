package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.PaymentApplicationDto;
import com.hhp.ConcertReservation.common.enums.AccountHistoryType;
import com.hhp.ConcertReservation.common.enums.ReservationStatus;
import com.hhp.ConcertReservation.common.enums.SeatStatus;
import com.hhp.ConcertReservation.domain.entity.*;
import com.hhp.ConcertReservation.domain.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
	final ReservationService reservationService;
	final SeatService seatService;
	final AccountService accountService;
	final AccountHistoryService accountHistoryService;
	final QueueService queueService;

	@Transactional
	public PaymentApplicationDto.processReservationPaymentResponse processReservationPayment(Long reservationId) {
		//예약 정보 조회
		Reservation reservation = reservationService.findById(reservationId);

		//예약 상태 유효성 확인
		reservation.validateReservationAvailability();

		//좌석 정보 조회
		Seat seat = seatService.findSeatById(reservation.getSeatId());

		//좌석 가격 확인
		Long price = seat.getPrice();

		//계좌 정보 조회
		Account account = accountService.findAccountByMemberId(reservation.getMemberId());

		//계좌 잔액 차감
		accountService.useBalance(account.getId(), price);

		//계좌 히스토리 생성
		AccountHistory history = accountHistoryService.createHistory(account.getId(), price, AccountHistoryType.USE);

		//예약 상태 변경
		reservation.setStatus(ReservationStatus.PAID.name());

		//좌석 상태 변경
		seat.setStatus(SeatStatus.PAID.name());

		//토큰 만료
		queueService.expireActiveTokenByMemberId(reservation.getMemberId());

		//변경사항 저장
		Reservation updatedReservation = reservationService.save(reservation);
		Seat updatedSeat = seatService.save(seat);
		Account updatedAccount = accountService.save(account);

		//반환
		return new PaymentApplicationDto.processReservationPaymentResponse(updatedReservation, updatedSeat, updatedAccount, history);
	}
}
