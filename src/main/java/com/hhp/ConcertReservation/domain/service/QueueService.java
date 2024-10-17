package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.common.enums.QueueStatus;
import com.hhp.ConcertReservation.domain.model.Queue;
import com.hhp.ConcertReservation.infra.persistence.QueueJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueService {
	private final QueueJpaRepository queueJpaRepository;

	/**
	 * 토큰 발급 메서드
	 * */
	public String generateToken() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 토큰 조회 메서드
	 */
	public Queue findQueueByToken(String token) {
		return queueJpaRepository
				       .findByToken(token)
				       .orElseThrow(() -> new IllegalArgumentException("토큰을 찾을 수 없습니다."));
	}

	/**
	 * 토큰 대기열 추가 메서드
	 */
	public Queue addToQueue(String token, Long memberId) {
		Queue queueEntry = new Queue();
		queueEntry.setToken(token);
		queueEntry.setMemberId(memberId);
		queueEntry.setStatus(QueueStatus.WAITING.toString());
		queueEntry.setExpiryAt(LocalDateTime.now().plusMinutes(Queue.QUEUE_TOKEN_EXPIRY_TIME));

		return queueJpaRepository.save(queueEntry);
	}

	/**
	 * 대기 순번 확인 메서드
	 */
	public Long getQueuePosition(String token) {
		return queueJpaRepository.findPositionInWaitingQueue(token)
				       .orElse(0L);
	}
}
