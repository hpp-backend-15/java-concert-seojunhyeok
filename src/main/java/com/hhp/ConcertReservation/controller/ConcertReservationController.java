package com.hhp.ConcertReservation.controller;

import com.hhp.ConcertReservation.application.dto.AccountApplicationDto;
import com.hhp.ConcertReservation.application.dto.PaymentApplicationDto;
import com.hhp.ConcertReservation.application.dto.QueueApplicationDto;
import com.hhp.ConcertReservation.application.dto.ReservationApplicationDto;
import com.hhp.ConcertReservation.application.facade.AccountFacade;
import com.hhp.ConcertReservation.application.facade.PaymentFacade;
import com.hhp.ConcertReservation.application.facade.QueueFacade;
import com.hhp.ConcertReservation.application.facade.ReservationFacade;
import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.domain.entity.ConcertSchedule;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.domain.service.AccountService;
import com.hhp.ConcertReservation.domain.service.ConcertScheduleService;
import com.hhp.ConcertReservation.domain.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "콘서트 예약 API", description = "콘서트 예약, 결제, 대기열 관리 API")
public class ConcertReservationController {

	private final ReservationFacade reservationFacade;
	private final QueueFacade queueFacade;
	private final PaymentFacade paymentFacade;
	private final AccountFacade accountFacade;
	private final AccountService accountService;
	private final ConcertScheduleService concertScheduleService;
	private final SeatService seatService;

	@Operation(summary = "대기열 추가 및 순번 조회", description = "사용자를 콘서트 예약 대기열에 추가하고 대기열 내 순번을 반환합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 대기열에 추가됨"),
			@ApiResponse(responseCode = "400", description = "잘못된 회원 ID")
	})
	@PostMapping("/queue")
	public ResponseEntity<QueueApplicationDto.getQueuePositionResponse> addToQueue(
			@Parameter(description = "대기열에 추가할 회원의 ID") @RequestParam Long memberId) {
		QueueApplicationDto.getQueuePositionResponse response = queueFacade.addToQueueAndGetQueuePosition(memberId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "대기열 순번 확인", description = "주어진 토큰과 연관된 사용자의 대기열 순번을 확인합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 대기열 순번을 조회함"),
			@ApiResponse(responseCode = "400", description = "잘못된 토큰")
	})
	@GetMapping("/queue")
	public ResponseEntity<QueueApplicationDto.getQueuePositionResponse> getQueuePosition(
			@Parameter(description = "사용자의 대기열 순번을 나타내는 토큰") @RequestHeader("TOKEN") String token) {
		QueueApplicationDto.getQueuePositionResponse response = queueFacade.getQueuePosition(token);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "좌석 예약", description = "사용자가 특정 콘서트 좌석을 예약합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "좌석이 성공적으로 예약됨"),
			@ApiResponse(responseCode = "400", description = "잘못된 회원 ID 또는 좌석 ID")
	})
	@PostMapping("/reservations")
	public ResponseEntity<ReservationApplicationDto.reserveSeatResponse> reserveSeat(
			@Parameter(description = "좌석을 예약할 회원의 ID") @RequestParam Long memberId,
			@Parameter(description = "예약할 좌석의 ID") @RequestParam Long seatId) {
		ReservationApplicationDto.reserveSeatResponse reserveSeatResponse = reservationFacade.reserveSeat(memberId, seatId);
		return new ResponseEntity<>(reserveSeatResponse, HttpStatus.CREATED);
	}

	@Operation(summary = "예약 결제 처리", description = "예약된 좌석에 대한 결제를 처리합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "결제가 성공적으로 처리됨"),
			@ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음")
	})
	@PostMapping("/reservations/{reservationId}/payment")
	public ResponseEntity<PaymentApplicationDto.processReservationPaymentResponse> processPayment(
			@Parameter(description = "결제를 처리할 예약의 ID") @PathVariable Long reservationId) {
		PaymentApplicationDto.processReservationPaymentResponse processReservationPaymentResponse = paymentFacade.processReservationPayment(reservationId);
		return ResponseEntity.ok(processReservationPaymentResponse);
	}

	@Operation(summary = "계좌 잔액 충전", description = "사용자의 계좌 잔액을 충전합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "계좌가 성공적으로 충전됨"),
			@ApiResponse(responseCode = "400", description = "잘못된 금액 또는 계좌 ID")
	})
	@PostMapping("/accounts/{accountId}/charge")
	public ResponseEntity<AccountApplicationDto.chargeBalanceResponse> chargeBalance(
			@Parameter(description = "충전할 계좌의 ID") @PathVariable Long accountId,
			@Parameter(description = "충전할 금액") @RequestParam Long amount) {
		AccountApplicationDto.chargeBalanceResponse chargeBalanceResponse = accountFacade.chargeBalance(accountId, amount);
		return ResponseEntity.ok(chargeBalanceResponse);
	}

	@Operation(summary = "계좌 잔액 조회", description = "사용자의 계좌 잔액을 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 계좌 잔액을 조회함"),
			@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
	})
	@GetMapping("/accounts/{accountId}")
	public ResponseEntity<Account> getBalance(
			@Parameter(description = "잔액을 조회할 계좌의 ID") @PathVariable Long accountId) {
		Account account = accountService.findAccountById(accountId);
		return ResponseEntity.ok(account);
	}

	@Operation(summary = "예약 가능한 콘서트 일정 조회", description = "특정 날짜 이후의 예약 가능한 콘서트 일정을 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 예약 가능한 콘서트 일정을 조회함")
	})
	@GetMapping("/concert-schedule/available/{dateTime}")
	public ResponseEntity<List<ConcertSchedule>> getAvailableConcertSchedules(
			@Parameter(description = "해당 날짜 이후의 예약 가능한 일정을 조회할 날짜와 시간") @PathVariable LocalDateTime dateTime) {
		List<ConcertSchedule> availableConcertSchedules = concertScheduleService.findAvailableConcertSchedules(dateTime);
		return ResponseEntity.ok(availableConcertSchedules);
	}

	@Operation(summary = "콘서트 일정의 예약 가능한 좌석 조회", description = "특정 콘서트 일정에 대한 예약 가능한 좌석을 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 예약 가능한 좌석을 조회함"),
			@ApiResponse(responseCode = "404", description = "콘서트 일정을 찾을 수 없음")
	})
	@GetMapping("/concert-schedule/{concertScheduleId}/seats/available")
	public ResponseEntity<List<Seat>> getAvailableConcertSchedules(
			@Parameter(description = "좌석을 조회할 콘서트 일정의 ID") @PathVariable Long concertScheduleId) {
		return ResponseEntity.ok(seatService.findAvailableSeats(concertScheduleId));
	}
}
