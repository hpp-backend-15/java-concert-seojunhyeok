package com.hhp.ConcertReservation.application.controller;

import com.hhp.ConcertReservation.application.dto.CreditChargeRequestDto;
import com.hhp.ConcertReservation.application.dto.CreditResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/credits")
public class CreditController {

	@PostMapping("/charge")
	public ResponseEntity<CreditResponseDto> chargeCredit(@RequestBody CreditChargeRequestDto creditChargeRequest) {
		return ResponseEntity.ok(new CreditResponseDto(creditChargeRequest.userId(), creditChargeRequest.amount() + 100.0));
	}

	@GetMapping("/balance/{userId}")
	public ResponseEntity<CreditResponseDto> getBalance(@PathVariable String userId, @RequestHeader("Authorization") String tokenHeader) {
		return ResponseEntity.ok(new CreditResponseDto(userId, 200.0));
	}
}