package com.hhp.ConcertReservation.application.controller;

import com.hhp.ConcertReservation.application.dto.PaymentRequestDto;
import com.hhp.ConcertReservation.application.dto.PaymentResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

	@PostMapping
	public ResponseEntity<PaymentResponseDto> makePayment(@RequestBody PaymentRequestDto paymentRequest, @RequestHeader("Authorization") String tokenHeader) {
		return ResponseEntity.ok(new PaymentResponseDto("payment-1", paymentRequest.reservationId(), paymentRequest.userId(), 100.0));
	}
}