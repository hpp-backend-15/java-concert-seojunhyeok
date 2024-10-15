package com.hhp.ConcertReservation.application.controller;

import com.hhp.ConcertReservation.application.dto.ReservationRequestDto;
import com.hhp.ConcertReservation.application.dto.ReservationResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
	@PostMapping
	public ResponseEntity<ReservationResponseDto> reserveSeat(@RequestBody ReservationRequestDto reservationRequest, @RequestHeader("Authorization") String tokenHeader) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new ReservationResponseDto("reservation-1", reservationRequest.scheduleId(), reservationRequest.seatId(), "user-1"));
	}
}