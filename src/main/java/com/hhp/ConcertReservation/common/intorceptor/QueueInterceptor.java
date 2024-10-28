package com.hhp.ConcertReservation.common.intorceptor;

import com.hhp.ConcertReservation.domain.service.QueueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class QueueInterceptor implements HandlerInterceptor {

	private final QueueService queueService;

	public QueueInterceptor(QueueService queueService) {
		this.queueService = queueService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = request.getHeader("TOKEN");

		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("토큰이 없습니다");
		}

		return queueService.isValidToken(token);
	}
}
