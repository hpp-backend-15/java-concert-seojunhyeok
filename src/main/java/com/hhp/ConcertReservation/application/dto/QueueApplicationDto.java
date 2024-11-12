package com.hhp.ConcertReservation.application.dto;

public class QueueApplicationDto {
	public record getQueuePositionResponse(
			String token,
			Long queuePosition
	){}
}
