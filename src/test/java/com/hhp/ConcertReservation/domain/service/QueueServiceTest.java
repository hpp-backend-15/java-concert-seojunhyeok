package com.hhp.ConcertReservation.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class QueueServiceTest {

	@Autowired
	private QueueService queueService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@BeforeEach
	void setup() {
		// Redis 데이터 초기화
		redisTemplate.getConnectionFactory().getConnection().flushAll();
	}

	@Test
	public void testAddToQueueAndRetrievePosition() {
		Long memberId = 123L;
		String token = queueService.generateToken();
		queueService.addToQueue(token, memberId);

		Long position = queueService.getQueuePosition(token);
		assertThat(position).isEqualTo(0L); // 첫 번째 추가된 항목은 0번 위치

		// 추가 항목을 추가한 뒤 위치 확인
		String anotherToken = queueService.generateToken();
		queueService.addToQueue(anotherToken, 456L);
		assertThat(queueService.getQueuePosition(anotherToken)).isEqualTo(1L); // 두 번째 항목은 1번 위치
	}

	@Test
	public void testActivateTokenAndIsValidToken() {
		Long memberId = 123L;
		String token = queueService.generateToken();
		queueService.addToQueue(token, memberId);
		queueService.activateToken(token);

		// Active Tokens에서 유효성 확인
		assertThat(queueService.isValidToken(token)).isTrue();
	}

	@Test
	public void testIsValidTokenThrowsExceptionForInvalidToken() {
		String invalidToken = UUID.randomUUID().toString();
		assertThrows(IllegalStateException.class, () -> queueService.isValidToken(invalidToken));
	}

	@Test
	public void testExpireOverdueTokensWithShortTTL() throws InterruptedException {
		Long memberId = 123L;
		String token = queueService.generateToken();
		queueService.addToQueue(token, memberId);
		queueService.activateToken(token);

		// TTL을 1초로 강제 설정
		redisTemplate.expire(token, Duration.ofSeconds(1));

		// 짧은 대기 시간 후 만료 처리
		Thread.sleep(1500); // 1.5초 대기 (TTL 이후 확인)

		queueService.expireOverdueTokens();

		// 만료된 토큰 확인 (존재하지 않아야 함)
		assertThrows(IllegalStateException.class, () -> queueService.isValidToken(token));
	}

	@Test
	public void testPassQueueEntries() {
		Long memberId1 = 1L;
		Long memberId2 = 2L;

		String token1 = queueService.generateToken();
		String token2 = queueService.generateToken();

		queueService.addToQueue(token1, memberId1);
		queueService.addToQueue(token2, memberId2);

		queueService.passQueueEntries(2);

		// 활성화된 항목 확인
		assertThat(queueService.isValidToken(token1)).isTrue();
		assertThat(queueService.isValidToken(token2)).isTrue();
	}

	@Test
	@DisplayName("expireActiveTokenByMemberId 메서드는 활성화된 토큰을 만료시킨다.")
	void testExpireActiveTokenByMemberId() {
		Long memberId = 1L;
		String token = queueService.generateToken();
		queueService.addToQueue(token, memberId);
		queueService.passQueueEntries(1); // 토큰을 활성 상태로 전환

		// Pre-condition: token이 ACTIVE_TOKENS_KEY에 존재하는지 확인
		boolean isTokenActive = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(QueueService.ACTIVE_TOKENS_KEY, token));
		assertThat(isTokenActive).isTrue();

		// 메서드 호출
		queueService.expireActiveTokenByMemberId(memberId);

		// Post-condition: token이 ACTIVE_TOKENS_KEY에서 제거되었는지 확인
		isTokenActive = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(QueueService.ACTIVE_TOKENS_KEY, token));
		assertThat(isTokenActive).isFalse();

		// 해시 데이터가 삭제되었는지 확인
		String storedMemberId = (String) redisTemplate.opsForHash().get(token, "memberId");
		assertThat(storedMemberId).isNull();
	}

}
