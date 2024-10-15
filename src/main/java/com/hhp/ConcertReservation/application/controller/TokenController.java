package com.hhp.ConcertReservation.application.controller;

import com.hhp.ConcertReservation.application.dto.TokenRequestDto;
import com.hhp.ConcertReservation.application.dto.TokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tokens")
public class TokenController {

	@PostMapping
	public ResponseEntity<TokenResponseDto> generateToken(@RequestBody TokenRequestDto tokenRequest) {
		return ResponseEntity.ok(new TokenResponseDto(tokenRequest.userId(), "sample-uuid", 1));
	}
}