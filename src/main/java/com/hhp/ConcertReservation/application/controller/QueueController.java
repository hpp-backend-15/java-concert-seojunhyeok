package com.hhp.ConcertReservation.application.controller;

import com.hhp.ConcertReservation.application.dto.QueuePositionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue/position")
public class QueueController {

	@GetMapping
	public ResponseEntity<QueuePositionDto> getQueuePosition(@RequestHeader("Authorization") String tokenHeader) {
		return ResponseEntity.ok(new QueuePositionDto(1));
	}
}