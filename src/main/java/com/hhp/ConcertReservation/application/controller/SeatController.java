package com.hhp.ConcertReservation.application.controller;

import com.hhp.ConcertReservation.application.dto.SeatResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/concert/{concertId}/schedules/{scheduleId}/seats")
public class SeatController {
	@GetMapping("/available")
	public ResponseEntity<List<SeatResponseDto>> getAvailableSeats(@PathVariable String concertId, @PathVariable String scheduleId, @RequestHeader("Authorization") String tokenHeader) {
		return ResponseEntity.ok(List.of(new SeatResponseDto("seat-1", 1), new SeatResponseDto("seat-2", 2)));
	}
}