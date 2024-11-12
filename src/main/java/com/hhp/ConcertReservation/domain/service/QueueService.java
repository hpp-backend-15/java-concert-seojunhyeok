// QueueService.java
package com.hhp.ConcertReservation.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueService {
	private final RedisTemplate<String, String> redisTemplate;
	public static final String WAITING_TOKENS_KEY = "waiting_tokens";
	public static final String ACTIVE_TOKENS_KEY = "active_tokens";
	public static final int QUEUE_TOKEN_EXPIRY_TIME = 5;

	public String generateToken() {
		return UUID.randomUUID().toString();
	}

	public void addToQueue(String token, Long memberId) {
		ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.add(WAITING_TOKENS_KEY, token, System.currentTimeMillis());
		redisTemplate.opsForHash().put(token, "memberId", memberId.toString());
	}

	public Long getQueuePosition(String token) {
		ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
		Long rank = zSetOperations.rank(WAITING_TOKENS_KEY, token);

		if (rank == null) {
			throw new NoSuchElementException("토큰을 찾을 수 없습니다.");
		}

		return rank;
	}

	public void activateToken(String token) {
		System.out.println("Activating token: " + token);
		redisTemplate.opsForSet().add(ACTIVE_TOKENS_KEY, token);
		redisTemplate.expire(token, Duration.ofMinutes(QUEUE_TOKEN_EXPIRY_TIME));
	}

	public boolean isValidToken(String token) {
		Boolean exists = redisTemplate.opsForSet().isMember(ACTIVE_TOKENS_KEY, token);
		if (exists == null || !exists) {
			throw new IllegalStateException("토큰이 유효하지 않습니다.");
		}
		return true;
	}

	public void expireOverdueTokens() {
		Objects.requireNonNull(redisTemplate.opsForSet().members(ACTIVE_TOKENS_KEY)).forEach(token -> {
			if (Boolean.FALSE.equals(redisTemplate.hasKey(token))) {
				redisTemplate.opsForSet().remove(ACTIVE_TOKENS_KEY, token);
			}
		});
	}

	public void passQueueEntries(int numberToPass) {
		ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

		var tokensToPass = zSetOperations.range(WAITING_TOKENS_KEY, 0, numberToPass - 1);
		if (tokensToPass != null) {
			tokensToPass.forEach(this::activateToken);
			tokensToPass.forEach(token -> zSetOperations.remove(WAITING_TOKENS_KEY, token));
			System.out.println("Tokens passed: " + tokensToPass);
		}
	}

	public void expireActiveTokenByMemberId(Long memberId) {
		Set<String> activeTokens = redisTemplate.opsForSet().members(ACTIVE_TOKENS_KEY);
		if (activeTokens != null) {
			for (String token : activeTokens) {
				String storedMemberId = (String) redisTemplate.opsForHash().get(token, "memberId");
				if (storedMemberId != null && storedMemberId.equals(memberId.toString())) {
					redisTemplate.opsForSet().remove(ACTIVE_TOKENS_KEY, token);
					redisTemplate.opsForHash().delete(token, "memberId");
					break;
				}
			}
		}
	}


}
