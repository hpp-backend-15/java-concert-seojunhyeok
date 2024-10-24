package com.hhp.ConcertReservation.controller.scheduler;

import com.hhp.ConcertReservation.domain.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class QueueProcessingScheduler {

	private final QueueService queueService;

	@Scheduled(fixedRate = 60000)
	public void processQueue() {
		//expiry_at 지난 토큰 만료
		queueService.expireOverdueQueues(LocalDateTime.now());

		//통과 가능한 인원 조회
		int newQueueEntriesCount = queueService.getNewQueueEntriesCount();

		//새로운 인원 대기열 통과
		if (newQueueEntriesCount > 0) {
			queueService.passQueueEntries(newQueueEntriesCount);
		}
	}
}
