package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.QueueApplicationDto;
import com.hhp.ConcertReservation.domain.entity.Queue;
import com.hhp.ConcertReservation.infra.persistence.QueueJpaRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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
}
