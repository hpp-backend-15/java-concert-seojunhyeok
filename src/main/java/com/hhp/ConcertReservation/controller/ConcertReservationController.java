package com.hhp.ConcertReservation.controller;

import com.hhp.ConcertReservation.application.dto.AccountApplicationDto;
import com.hhp.ConcertReservation.application.dto.PaymentApplicationDto;
import com.hhp.ConcertReservation.application.dto.QueueApplicationDto;
import com.hhp.ConcertReservation.application.dto.ReservationApplicationDto;
import com.hhp.ConcertReservation.application.facade.AccountFacade;
import com.hhp.ConcertReservation.application.facade.PaymentFacade;
import com.hhp.ConcertReservation.application.facade.QueueFacade;
import com.hhp.ConcertReservation.application.facade.ReservationFacade;
import com.hhp.ConcertReservation.controller.dto.RequestDto;
import com.hhp.ConcertReservation.controller.dto.ResponseDto;
import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.domain.entity.ConcertSchedule;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.domain.service.AccountService;
import com.hhp.ConcertReservation.domain.service.ConcertScheduleService;
import com.hhp.ConcertReservation.domain.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConcertReservationController implements com.hhp.ConcertReservation.controller.ConcertReservationApi {

	private final ReservationFacade reservationFacade;
	private final QueueFacade queueFacade;
	private final PaymentFacade paymentFacade;
	private final AccountFacade accountFacade;
	private final AccountService accountService;
	private final ConcertScheduleService concertScheduleService;
	private final SeatService seatService;

	@Override
	@PostMapping("/queue")
	public ResponseEntity<ResponseDto.QueueResponse> addToQueue(RequestDto.PostQueue requestDto) {
		QueueApplicationDto.getQueuePositionResponse result = queueFacade.addToQueueAndGetQueuePosition(requestDto.memberId());
		ResponseDto.QueueResponse response = new ResponseDto.QueueResponse(result.queue().getToken(), result.queue().getStatus(), result.queuePosition());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	@GetMapping("/queue")
	public ResponseEntity<ResponseDto.QueueResponse> getQueuePosition(@RequestHeader String token) {
		QueueApplicationDto.getQueuePositionResponse result = queueFacade.getQueuePosition(token);
		ResponseDto.QueueResponse response = new ResponseDto.QueueResponse(result.queue().getToken(), result.queue().getStatus(), result.queuePosition());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	@PostMapping("/reservations")
	public ResponseEntity<ResponseDto.ReserveSeat> reserveSeat(RequestDto.reserveSeat requestDto) {
		ReservationApplicationDto.reserveSeatResponse reserveSeatResponse = reservationFacade.reserveSeat(requestDto.memberId(), requestDto.seatId());
		ResponseDto.ReserveSeat response = new ResponseDto.ReserveSeat(reserveSeatResponse.reservation().getId());

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Override
	@PostMapping("/payment")
	public ResponseEntity<ResponseDto.ProcessPayment> processPayment(RequestDto.processPayment requestDto) {
		PaymentApplicationDto.processReservationPaymentResponse result = paymentFacade.processReservationPayment(requestDto.reservationId());
		ResponseDto.ProcessPayment response = new ResponseDto.ProcessPayment(result.history().getAccountId(), result.history().getAmount(), result.history().getType());

		return ResponseEntity.ok(response);
	}

	@Override
	@PostMapping("/charge")
	public ResponseEntity<ResponseDto.ChargeBalance> chargeBalance(RequestDto.ChargeBalance requestDto) {
		AccountApplicationDto.chargeBalanceResponse result = accountFacade.chargeBalance(requestDto.memberId(), requestDto.amount());
		ResponseDto.ChargeBalance response = new ResponseDto.ChargeBalance(result.history().getAccountId(), result.history().getAmount(), result.history().getType());

		return ResponseEntity.ok(response);
	}

	@Override
	@GetMapping("/accounts/{accountId}")
	public ResponseEntity<ResponseDto.GetBalance> getBalance(@PathVariable Long accountId) {
		Account account = accountService.findAccountById(accountId);
		ResponseDto.GetBalance response = new ResponseDto.GetBalance(account.getId(), account.getBalance());

		return ResponseEntity.ok(response);
	}

	@Override
	@GetMapping("/concert-schedule/available/{dateTime}")
	public ResponseEntity<ResponseDto.GetAvailableConcertSchedules> getAvailableConcertSchedules(@PathVariable LocalDateTime dateTime) {
		List<ConcertSchedule> result = concertScheduleService.findAvailableConcertSchedules(dateTime);
		ResponseDto.GetAvailableConcertSchedules response = new ResponseDto.GetAvailableConcertSchedules(result);

		return ResponseEntity.ok(response);
	}

	@Override
	@GetMapping("/concert-schedule/{concertScheduleId}/seats/available")
	public ResponseEntity<ResponseDto.GetAvailableSeat> getAvailableSeat(@PathVariable Long concertScheduleId) {
		List<Seat> result = seatService.findAvailableSeats(concertScheduleId);
		ResponseDto.GetAvailableSeat response = new ResponseDto.GetAvailableSeat(result);

		return ResponseEntity.ok(response);
	}
}
