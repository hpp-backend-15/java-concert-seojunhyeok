package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.PaymentApplicationDto;
import com.hhp.ConcertReservation.common.enums.QueueStatus;
import com.hhp.ConcertReservation.common.enums.ReservationStatus;
import com.hhp.ConcertReservation.common.enums.SeatStatus;
import com.hhp.ConcertReservation.domain.entity.*;
import com.hhp.ConcertReservation.domain.service.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase
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

	Reservation reservation;
	Reservation canceledReservation;
	Seat seat;
	Account account;
	Queue queue;
	Member member;

	@BeforeEach
	void setUp() {
		seat = new Seat();
		seat.setConcertScheduleId(1L);
		seat.setSeatNumber(1);
		seat.setStatus(SeatStatus.AVAILABLE.name());
		seat.setPrice(100L);
		seat = seatService.save(seat);

		reservation = new Reservation();
		reservation.setMemberId(1L);
		reservation.setSeatId(seat.getId());
		reservation.setStatus(ReservationStatus.RESERVED.name());
		reservation.setExpiryAt(LocalDateTime.now().plusMinutes(5));
		reservation = reservationService.save(reservation);

		canceledReservation = new Reservation();
		canceledReservation.setMemberId(2L);
		canceledReservation.setSeatId(seat.getId());
		canceledReservation.setStatus(ReservationStatus.CANCELED.name());
		canceledReservation.setExpiryAt(LocalDateTime.now().plusMinutes(5));
		canceledReservation = reservationService.save(canceledReservation);

		account = new Account();
		account.setMemberId(1L);
		account.setBalance(500L);
		account = accountService.save(account);

		queue = new Queue();
		queue.setMemberId(1L);
		queue.setToken("testToken");
		queue.setStatus(QueueStatus.WAITING.name());
		queue = queueService.addToQueue(queue.getToken(), queue.getMemberId());
	}

	@Test
	@DisplayName("성공적으로 예약 결제를 처리할 수 있다.")
	@Transactional
	void processReservationPayment_success() {
		//when
		PaymentApplicationDto.processReservationPaymentResponse response = paymentFacade.processReservationPayment(reservation.getId());

		assertThat(response).isNotNull();
		assertThat(response.reservation().getStatus()).isEqualTo("PAID");
		assertThat(response.seat().getStatus()).isEqualTo("PAID");
		assertThat(response.account().getBalance()).isEqualTo(400L);
		assertThat(response.queue().getStatus()).isEqualTo("EXPIRED");

		var histories = accountHistoryService.findAllByAccountId(account.getId());
		assertThat(histories).hasSize(1);
		assertThat(histories.get(0).getType()).isEqualTo("USE");
		assertThat(histories.get(0).getAmount()).isEqualTo(100L);
	}

	@Test
	@DisplayName("존재하지 않는 예약 ID로 결제를 시도하면 예외가 발생한다.")
	@Transactional
	void processReservationPayment_reservationNotFound() {
		//given
		Long invalidReservationId = Long.MAX_VALUE;

		//when & then
		assertThrows(NoSuchElementException.class, () -> paymentFacade.processReservationPayment(invalidReservationId));
		var histories = accountHistoryService.findAllByAccountId(account.getId());
		assertThat(histories).isEmpty();
	}

	@Test
	@DisplayName("잔액이 부족한 경우 결제를 시도하면 예외가 발생한다.")
	@Transactional
	void processReservationPayment_insufficientBalance() {
		account.setBalance(50L);  // 잔액 부족 설정
		accountService.save(account);  // 잔액 업데이트

		assertThrows(IllegalStateException.class, () -> paymentFacade.processReservationPayment(reservation.getId()));

		var histories = accountHistoryService.findAllByAccountId(account.getId());
		assertThat(histories).isEmpty();
	}

	@Test
	@DisplayName("이미 결제 되었거나 취소 된 예약 ID로 결제를 시도하면 예외가 발생한다.")
	@Transactional
	void processReservationPayment_invalidReservationId() {

		assertThrows(IllegalStateException.class, () -> paymentFacade.processReservationPayment(canceledReservation.getId()));

		var histories = accountHistoryService.findAllByAccountId(account.getId());
		assertThat(histories).isEmpty();
	}
}
