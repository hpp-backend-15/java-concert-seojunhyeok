package com.hhp.ConcertReservation.controller.dto;

public class RequestDto {
	public record PostQueue(Long memberId) {}

	public record reserveSeat(Long memberId, Long seatId) {}

	public record processPayment(Long reservationId) {}

	public record ChargeBalance(Long memberId, Long amount) {}

}
