package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.common.enums.QueueStatus;
import com.hhp.ConcertReservation.domain.entity.Queue;
import com.hhp.ConcertReservation.infra.persistence.QueueJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueService {
	private final QueueJpaRepository queueJpaRepository;

	public String generateToken() {
		return UUID.randomUUID().toString();
	}

	public Queue findQueueByToken(String token) {
		return queueJpaRepository
				       .findByToken(token)
				       .orElseThrow(() -> new NoSuchElementException("토큰을 찾을 수 없습니다."));
	}

	public Queue findQueueByMemberId(Long memberId) {
		return queueJpaRepository
				       .findByMemberId(memberId)
				       .orElseThrow(() -> new NoSuchElementException("토큰을 찾을 수 없습니다."));
	}

	public Queue addToQueue(String token, Long memberId) {
		Queue queueEntry = new Queue();
		queueEntry.setToken(token);
		queueEntry.setMemberId(memberId);
		queueEntry.setStatus(QueueStatus.WAITING.name());

		return queueJpaRepository.save(queueEntry);
	}

	public Long getQueuePosition(String token) {
		return queueJpaRepository.findPositionInWaitingQueue(token)
				       .orElse(0L);
	}

	public int getNewQueueEntriesCount() {
		long passedCount = queueJpaRepository
				                   .countByStatus(QueueStatus.ENTERED.name())
				                   .orElse(0L);
		return Queue.MAX_ALLOWED_QUEUE_PASS - (int) passedCount;
	}

	public void expireOverdueQueues(LocalDateTime dateTime) {
		List<Queue> expireOverdueQueues = queueJpaRepository.findExpireOverdueQueues(dateTime);

		for (Queue queue : expireOverdueQueues) {
			queue.setStatus(QueueStatus.EXPIRED.name());
			queueJpaRepository.save(queue);
		}
	}

	public void passQueueEntries(int numberToPass) {
		Pageable pageable = PageRequest.of(0, numberToPass);
		List<Queue> waitingEntries = queueJpaRepository.findTopByStatusOrderByIdAsc(QueueStatus.WAITING.name(), pageable);

		for (Queue entry : waitingEntries) {
			entry.expireToken();
			queueJpaRepository.save(entry);
		}
	}

	public Queue save(Queue queue) {
		return queueJpaRepository.save(queue);
	}
}
