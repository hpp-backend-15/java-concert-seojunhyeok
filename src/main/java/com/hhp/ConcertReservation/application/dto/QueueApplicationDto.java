package com.hhp.ConcertReservation.application.dto;

import com.hhp.ConcertReservation.domain.entity.Queue;

public class QueueApplicationDto {
	public record getQueuePositionResponse(
			Queue queue,
			Long queuePosition
	){}
}
