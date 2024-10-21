package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.PaymentApplicationDto;
import com.hhp.ConcertReservation.common.enums.AccountHistoryType;
import com.hhp.ConcertReservation.domain.model.Account;
import com.hhp.ConcertReservation.domain.model.Queue;
import com.hhp.ConcertReservation.domain.model.Reservation;
import com.hhp.ConcertReservation.domain.model.Seat;
import com.hhp.ConcertReservation.domain.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PaymentFacadeIntegrationTest {

	@Autowired
	private PaymentFacade paymentFacade;

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private SeatService seatService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountHistoryService accountHistoryService;

	@Autowired
	private QueueService queueService;

	private Reservation reservation;
	private Seat seat;
	private Account account;
	private Queue queue;

	@BeforeEach
	void setUp() {
		reservation = new Reservation();
		reservation.setMemberId(1L);
		reservation.setSeatId(1L);
		reservation.setStatus("PENDING");
		reservation.setExpiryAt(LocalDateTime.now().plusMinutes(5));
		reservation = reservationService.save(reservation);

		seat = new Seat();
		seat.setConcertScheduleId(1L);
		seat.setSeatNumber(1);
		seat.setStatus("AVAILABLE");
		seat.setPrice(100L);
		seat = seatService.save(seat);

		account = new Account();
		account.setMemberId(1L);
		account.setBalance(500L);
		account = accountService.createAccount(account);

		queue = new Queue();
		queue.setMemberId(1L);
		queue.setToken("testToken");
		queue.setStatus("WAITING");
		queue = queueService.addToQueue(queue.getToken(), queue.getMemberId());
	}

	@Test
	@DisplayName("성공적으로 예약 결제를 처리할 수 있다.")
	void processReservationPayment_success() {
		PaymentApplicationDto.processReservationPaymentResponse response = paymentFacade.processReservationPayment(1L);

		assertThat(response).isNotNull();
		assertThat(response.reservation().getStatus()).isEqualTo("CONFIRMED");
		assertThat(response.seat().getStatus()).isEqualTo("PAID");
		assertThat(response.account().getBalance()).isEqualTo(400L);  // 잔액 차감
		assertThat(response.queue().getStatus()).isEqualTo("EXPIRED");

		// 실제로 AccountHistoryService가 기록되었는지 확인
		var histories = accountHistoryService.findAllByAccountId(account.getId());
		assertThat(histories).hasSize(1);
		assertThat(histories.get(0).getType()).isEqualTo(AccountHistoryType.USE);
		assertThat(histories.get(0).getAmount()).isEqualTo(100L);
	}

	@Test
	@DisplayName("존재하지 않는 예약 ID로 결제를 시도하면 예외가 발생한다.")
	void processReservationPayment_reservationNotFound() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			paymentFacade.processReservationPayment(2L);
		});

		assertThat(exception.getMessage()).isEqualTo("예약 정보를 찾을 수 없습니다.");
		var histories = accountHistoryService.findAllByAccountId(account.getId());
		assertThat(histories).isEmpty();
	}

	@Test
	@DisplayName("잔액이 부족한 경우 결제를 시도하면 예외가 발생한다.")
	void processReservationPayment_insufficientBalance() {
		account.setBalance(50L);  // 잔액 부족 설정
		accountService.updateAccount(account);  // 잔액 업데이트

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			paymentFacade.processReservationPayment(1L);
		});

		assertThat(exception.getMessage()).isEqualTo("잔여 포인트가 부족합니다. 현재 잔액: 50");
		var histories = accountHistoryService.findAllByAccountId(account.getId());
		assertThat(histories).isEmpty();
	}

	@Test
	@DisplayName("이미 결제된 좌석으로 결제를 시도하면 예외가 발생한다.")
	void processReservationPayment_seatAlreadyPaid() {
		seat.setStatus("PAID");  // 좌석 이미 결제 상태
		seatService.updateSeat(seat);  // 좌석 상태 업데이트

		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			paymentFacade.processReservationPayment(1L);
		});

		assertThat(exception.getMessage()).isEqualTo("해당 좌석은 이미 예약되었습니다.");
		var histories = accountHistoryService.findAllByAccountId(account.getId());
		assertThat(histories).isEmpty();
	}
}
