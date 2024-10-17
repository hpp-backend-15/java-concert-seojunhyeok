package com.hhp.ConcertReservation.application.scheduler;

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
		int newQueueEntriesCount = queueService.getNewQueueEntriesCount();

		if (newQueueEntriesCount > 0) {
			queueService.passQueueEntries(newQueueEntriesCount);
		}
	}
}
