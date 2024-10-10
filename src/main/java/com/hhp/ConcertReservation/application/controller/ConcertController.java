package com.hhp.ConcertReservation.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/concerts")
public class ConcertController {
	@GetMapping("/{concertId}/schedules/available")
	public ResponseEntity<List<String>> getAvailableDates(@PathVariable String concertId, @RequestHeader("Authorization") String tokenHeader) {
		return ResponseEntity.ok(List.of("2024-10-11", "2024-10-12"));
	}
}