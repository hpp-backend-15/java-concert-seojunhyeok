package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.common.enums.QueueStatus;
import com.hhp.ConcertReservation.domain.entity.Queue;
import com.hhp.ConcertReservation.infra.persistence.QueueJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class QueueServiceTest {
	@Mock
	private QueueJpaRepository queueJpaRepository;

	@InjectMocks
	private QueueService queueService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("유효한 토큰이 있을 때 올바른 대기열 순번을 반환해야 한다")
	void getQueuePosition_ShouldReturnPosition_WhenTokenExists() {
		//given
		String token = "valid-token";
		Long expectedPosition = 5L;

		when(queueJpaRepository.findPositionInWaitingQueue(token))
				.thenReturn(Optional.of(expectedPosition));

		//when
		Long actualPosition = queueService.getQueuePosition(token);

		//then
		assertEquals(expectedPosition, actualPosition);
	}


	@Test
	@DisplayName("토큰이 대기열에 없을 때 0을 반환해야 한다")
	void getQueuePosition_ShouldReturnZero_WhenTokenDoesNotExist() {
		// given
		String token = "non-existent-token";
		Long expectedPosition = 0L;

		when(queueJpaRepository.findPositionInWaitingQueue(token))
				.thenReturn(Optional.empty());

		// when
		Long actualPosition = queueService.getQueuePosition(token);

		// then
		assertEquals(expectedPosition, actualPosition);
	}

	@Test
	@DisplayName("유효한 토큰이 있을 때 올바른 대기열 상태를 반환해야 한다")
	void getQueueStatus_ShouldReturnStatus_WhenTokenExists() {
		// given
		String token = "valid-token";
		Queue queue = new Queue();
		queue.setToken(token);
		queue.setStatus(QueueStatus.WAITING.name());

		when(queueJpaRepository.findByToken(token))
				.thenReturn(Optional.of(queue));

		// when
		Queue actualQueue = queueService.findQueueByToken(token);

		// then
		assertEquals(QueueStatus.WAITING.name(), actualQueue.getStatus());
	}

	@Test
	@DisplayName("토큰이 대기열에 없을 때 NoSuchElementException을 던져야 한다")
	void getQueueStatus_ShouldThrowException_WhenTokenDoesNotExist() {
		// given
		String token = "invalid-token";

		when(queueJpaRepository.findByToken(token))
				.thenReturn(Optional.empty());

		// when & then
		NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
			queueService.findQueueByToken(token);
		});

		assertEquals("토큰을 찾을 수 없습니다.", exception.getMessage());
	}
}