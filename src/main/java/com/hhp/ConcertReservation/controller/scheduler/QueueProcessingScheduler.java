package com.hhp.ConcertReservation.controller.scheduler;

import com.hhp.ConcertReservation.domain.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueProcessingScheduler {

	private final QueueService queueService;

	@Scheduled(fixedRate = 60000)
	public void processQueue() {
		//expiry_at 지난 토큰 만료
		queueService.expireOverdueTokens();

		//고정된 수만큼 들여보냄
		queueService.passQueueEntries(10);
	}
}
