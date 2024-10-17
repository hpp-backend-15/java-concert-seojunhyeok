package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.common.enums.QueueStatus;
import com.hhp.ConcertReservation.domain.model.Queue;
import com.hhp.ConcertReservation.infra.persistence.QueueJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
	 * 토큰 조회 메서드 - 토큰
	 */
	public Queue findQueueByToken(String token) {
		return queueJpaRepository
				       .findByToken(token)
				       .orElseThrow(() -> new IllegalArgumentException("토큰을 찾을 수 없습니다."));
	}

	/**
	 * 토큰 조회 메서드 - 멤버 ID
	 */
	public Queue findQueueByMemberId(Long memberId) {
		return queueJpaRepository
				       .findByMemberId(memberId)
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

		return queueJpaRepository.save(queueEntry);
	}

	/**
	 * 대기 순번 확인 메서드
	 */
	public Long getQueuePosition(String token) {
		return queueJpaRepository.findPositionInWaitingQueue(token)
				       .orElse(0L);
	}

	/**
	 * 새로 대기열에 추가할 인원을 구하는 메서드 (Queue.MAX_ALLOWED_QUEUE_PASS - 토큰이 만료되지 않은 현재 대기열 통과자 = 새로 대기열에 추가할 인원)
	 */
	public int getNewQueueEntriesCount() {
		long passedCount = queueJpaRepository
				                   .countByStatus(QueueStatus.ENTERED.toString())
				                   .orElse(0L);
		return Queue.MAX_ALLOWED_QUEUE_PASS - (int) passedCount;
	}

	/**
	 * 매개변수로 받은 인원수만큼 대기열을 통과시키는 메서드
	 * */
	public void passQueueEntries(int numberToPass) {
		Pageable pageable = PageRequest.of(0, numberToPass);
		List<Queue> waitingEntries = queueJpaRepository.findTopByStatusOrderByIdAsc(QueueStatus.WAITING.toString(), pageable);

		for (Queue entry : waitingEntries) {
			entry.expireToken();
			queueJpaRepository.save(entry);
		}
	}

}
