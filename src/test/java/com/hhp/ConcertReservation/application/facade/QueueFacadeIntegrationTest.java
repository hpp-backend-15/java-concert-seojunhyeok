package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.application.dto.QueueApplicationDto;
import com.hhp.ConcertReservation.domain.service.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
class QueueFacadeIntegrationTest {

	@Autowired
	private QueueFacade queueFacade;

	@Autowired
	private QueueService queueService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@BeforeEach
	void setUp() {
		// Redis 초기화 - 각 테스트 전에 Redis 상태를 초기화하여 테스트 격리 보장
		redisTemplate.delete(QueueService.WAITING_TOKENS_KEY);
		redisTemplate.delete(QueueService.ACTIVE_TOKENS_KEY);
	}

	@Test
	@DisplayName("성공적으로 대기열에 추가하고 대기 순번을 확인할 수 있다.")
	void addToQueueAndGetQueuePosition_success() {
		// 대기열에 추가 및 순번 확인
		QueueApplicationDto.getQueuePositionResponse response = queueFacade.addToQueueAndGetQueuePosition(1L);

		assertThat(response).isNotNull();
		assertThat(response.token()).isNotNull();
		assertThat(response.queuePosition()).isEqualTo(0L); // 새로 추가된 항목은 0번 순번
	}

	@Test
	@DisplayName("토큰으로 대기 순번을 조회할 수 있다.")
	void getQueuePosition_success() {
		//given
		String token = queueService.generateToken();
		queueService.addToQueue(token, 1L);

		//when
		QueueApplicationDto.getQueuePositionResponse response = queueFacade.getQueuePosition(token);

		//then
		assertThat(response).isNotNull();
		assertThat(response.token()).isEqualTo(token);
		assertThat(response.queuePosition()).isEqualTo(0L);
	}

	@Test
	@DisplayName("존재하지 않는 토큰으로 대기 순번을 조회할 때 예외가 발생한다.")
	void getQueuePosition_tokenNotFound() {
		assertThrows(NoSuchElementException.class, () -> {
			queueFacade.getQueuePosition("invalidToken");
		});
	}

	@Test
	@DisplayName("만료시간이 지난 Queue를 만료")
	void expireOverdueTokens() {
		// Given: 만료되지 않은 토큰과 만료된 토큰을 추가
		String activeToken = queueService.generateToken();
		String expiredToken = queueService.generateToken();

		// 활성화 상태로 토큰 추가
		queueService.addToQueue(activeToken, 1L);
		queueService.passQueueEntries(1); // activeToken 활성화
		queueService.addToQueue(expiredToken, 2L);
		queueService.passQueueEntries(1); // expiredToken 활성화

		// Redis에서 만료 시간을 강제로 조정 (activeToken은 유지, expiredToken은 만료)
		redisTemplate.expire(expiredToken, Duration.ofSeconds(0));

		// When: 만료된 토큰들을 정리
		queueService.expireOverdueTokens();

		// Then: activeToken은 유지되고, expiredToken은 제거되었는지 확인
		boolean isActiveTokenStillActive = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(QueueService.ACTIVE_TOKENS_KEY, activeToken));
		boolean isExpiredTokenStillActive = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(QueueService.ACTIVE_TOKENS_KEY, expiredToken));

		assertThat(isActiveTokenStillActive).isTrue();
		assertThat(isExpiredTokenStillActive).isFalse();
	}
}
