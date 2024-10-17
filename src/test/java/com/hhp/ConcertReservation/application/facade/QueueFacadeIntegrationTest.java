package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.QueueApplicationDto;
import com.hhp.ConcertReservation.domain.model.Queue;
import com.hhp.ConcertReservation.domain.service.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class QueueFacadeIntegrationTest {

	@Autowired
	private QueueFacade queueFacade;

	@Autowired
	private QueueService queueService;

	private Queue queue;

	@BeforeEach
	void setUp() {
		queue = new Queue();
		queue.setId(1L);
		queue.setMemberId(1L);
		queue.setToken("testToken");
		queue.setStatus("WAITING");

		// Mock 설정
		when(queueService.generateToken()).thenReturn("testToken");
		when(queueService.addToQueue("testToken", 1L)).thenReturn(queue);
		when(queueService.getQueuePosition("testToken")).thenReturn(1L);
		when(queueService.findQueueByToken("testToken")).thenReturn(queue);
	}

	@Test
	@DisplayName("성공적으로 대기열에 추가하고 대기 순번을 확인할 수 있다.")
	void addToQueueAndGetQueuePosition_success() {
		QueueApplicationDto.getQueuePositionResponse response = queueFacade.addToQueueAndGetQueuePosition(1L);

		assertThat(response).isNotNull();
		assertThat(response.queue().getMemberId()).isEqualTo(1L);
		assertThat(response.queuePosition()).isEqualTo(1L);
		verify(queueService, times(1)).addToQueue("testToken", 1L);
		verify(queueService, times(1)).getQueuePosition("testToken");
	}

	@Test
	@DisplayName("토큰으로 대기 순번을 조회할 수 있다.")
	void getQueuePosition_success() {
		QueueApplicationDto.getQueuePositionResponse response = queueFacade.getQueuePosition("testToken");

		assertThat(response).isNotNull();
		assertThat(response.queue().getToken()).isEqualTo("testToken");
		assertThat(response.queuePosition()).isEqualTo(1L);
		verify(queueService, times(1)).findQueueByToken("testToken");
		verify(queueService, times(1)).getQueuePosition("testToken");
	}

	@Test
	@DisplayName("존재하지 않는 토큰으로 대기 순번을 조회할 때 예외가 발생한다.")
	void getQueuePosition_tokenNotFound() {
		when(queueService.findQueueByToken("invalidToken")).thenThrow(new IllegalArgumentException("토큰을 찾을 수 없습니다."));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			queueFacade.getQueuePosition("invalidToken");
		});

		assertThat(exception.getMessage()).isEqualTo("토큰을 찾을 수 없습니다.");
		verify(queueService, never()).getQueuePosition("invalidToken");
	}

	@Test
	@DisplayName("멤버 ID로 대기열에 추가할 때 대기 순번을 확인할 수 있다.")
	void addToQueue_memberId_success() {
		when(queueService.addToQueue("testToken", 2L)).thenReturn(queue);
		when(queueService.getQueuePosition("testToken")).thenReturn(2L);

		QueueApplicationDto.getQueuePositionResponse response = queueFacade.addToQueueAndGetQueuePosition(2L);

		assertThat(response).isNotNull();
		assertThat(response.queue().getMemberId()).isEqualTo(2L);
		assertThat(response.queuePosition()).isEqualTo(2L);
		verify(queueService, times(1)).addToQueue("testToken", 2L);
		verify(queueService, times(1)).getQueuePosition("testToken");
	}

	@Test
	@DisplayName("대기열 추가 중 예외가 발생할 경우 예외를 처리한다.")
	void addToQueue_exceptionHandling() {
		when(queueService.addToQueue("testToken", 3L)).thenThrow(new RuntimeException("대기열 추가 중 오류가 발생했습니다."));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			queueFacade.addToQueueAndGetQueuePosition(3L);
		});

		assertThat(exception.getMessage()).isEqualTo("대기열 추가 중 오류가 발생했습니다.");
		verify(queueService, never()).getQueuePosition("testToken");
	}
}
