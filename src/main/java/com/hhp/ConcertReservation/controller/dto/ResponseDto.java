package com.hhp.ConcertReservation.controller.dto;

import com.hhp.ConcertReservation.domain.entity.ConcertSchedule;
import com.hhp.ConcertReservation.domain.entity.Seat;

import java.util.List;

public class ResponseDto {
	public record QueueResponse(String token, String status, Long queuePosition) {}

	public record ReserveSeat(Long reservationId) {}

	public record ProcessPayment(Long accountId, Long amount, String type) {}

	public record ChargeBalance(Long accountId, Long amount, String type) {}

	public record GetBalance(Long accountId, Long amount) {}

	public record GetAvailableConcertSchedules(List<ConcertSchedule> list) {}

	public record GetAvailableSeat(List<Seat> list) {}
}
