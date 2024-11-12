// QueueFacade.java
package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.QueueApplicationDto;
import com.hhp.ConcertReservation.domain.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class QueueFacade {
	private final QueueService queueService;

	@Transactional
	public QueueApplicationDto.getQueuePositionResponse addToQueueAndGetQueuePosition(Long memberId) {
		String token = queueService.generateToken();
		queueService.addToQueue(token, memberId);
		Long queuePosition = queueService.getQueuePosition(token);

		return new QueueApplicationDto.getQueuePositionResponse(token, queuePosition);
	}

	@Transactional
	public QueueApplicationDto.getQueuePositionResponse getQueuePosition(String token) {
		Long queuePosition = queueService.getQueuePosition(token);
		return new QueueApplicationDto.getQueuePositionResponse(token, queuePosition);
	}
}
