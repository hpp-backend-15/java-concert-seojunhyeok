package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.QueueApplicationDto;
import com.hhp.ConcertReservation.common.enums.QueueStatus;
import com.hhp.ConcertReservation.domain.entity.Queue;
import com.hhp.ConcertReservation.domain.service.QueueService;
import com.hhp.ConcertReservation.infra.persistence.QueueJpaRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase
@SpringBootTest
@Transactional
class QueueFacadeIntegrationTest {

	@Autowired
	private QueueFacade queueFacade;

	@Autowired
	private QueueService queueService;

	@Autowired
	private QueueJpaRepository queueJpaRepository;

	@Test
	@DisplayName("성공적으로 대기열에 추가하고 대기 순번을 확인할 수 있다.")
	void addToQueueAndGetQueuePosition_success() {
		QueueApplicationDto.getQueuePositionResponse response = queueFacade.addToQueueAndGetQueuePosition(1L);

		assertThat(response).isNotNull();
		assertThat(response.queue().getMemberId()).isEqualTo(1L);
		assertThat(response.queuePosition()).isEqualTo(1L);
	}

	@Test
	@DisplayName("토큰으로 대기 순번을 조회할 수 있다.")
	void getQueuePosition_success() {
		//given
		Queue queue = new Queue();
		queue.setId(1L);
		queue.setMemberId(1L);
		queue.setToken("testToken");
		queue.setStatus("WAITING");
		queueJpaRepository.save(queue);

		//when
		QueueApplicationDto.getQueuePositionResponse response = queueFacade.getQueuePosition("testToken");

		//then
		assertThat(response).isNotNull();
		assertThat(response.queue().getToken()).isEqualTo("testToken");
		assertThat(response.queuePosition()).isEqualTo(1L);
	}

	@Test
	@DisplayName("존재하지 않는 토큰으로 대기 순번을 조회할 때 예외가 발생한다.")
	void getQueuePosition_tokenNotFound() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			queueFacade.getQueuePosition("invalidToken");
		});

		assertThat(exception.getMessage()).isEqualTo("토큰을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("만료시간이 지난 Queue 만료")
	public void testExpireOverdueQueues() {
		// Given: 만료 시간이 지난 Queue가 있는 상태
		LocalDateTime now = LocalDateTime.now();

		// 테스트에 사용할 만료되지 않은 Queue 엔티티를 DB에 저장
		Queue validQueue = new Queue();
		validQueue.setToken("valid-token");
		validQueue.setMemberId(1L);
		validQueue.setExpiryAt(now.plusMinutes(10)); // 10분 후 만료
		validQueue.setStatus(QueueStatus.ENTERED.name());
		queueJpaRepository.save(validQueue);

		// 테스트에 사용할 만료된 Queue 엔티티를 DB에 저장
		Queue expiredQueue = new Queue();
		expiredQueue.setToken("expired-token");
		expiredQueue.setMemberId(2L);
		expiredQueue.setExpiryAt(now.minusMinutes(10)); // 10분 전 만료
		expiredQueue.setStatus(QueueStatus.ENTERED.name());
		queueJpaRepository.save(expiredQueue);

		// When: expireOverdueQueues 메소드 호출
		queueService.expireOverdueQueues(now);  // 테스트에서 동일한 'now' 변수 사용

		// Then: 만료된 Queue의 상태가 'EXPIRED'로 변경되었는지 확인
		validQueue = queueJpaRepository.findByToken("valid-token").orElseThrow();
		assertThat(validQueue.getStatus()).isEqualTo(QueueStatus.ENTERED.name());

		expiredQueue = queueJpaRepository.findByToken("expired-token").orElseThrow();
		assertThat(expiredQueue.getStatus()).isEqualTo(QueueStatus.EXPIRED.name());
	}
}
