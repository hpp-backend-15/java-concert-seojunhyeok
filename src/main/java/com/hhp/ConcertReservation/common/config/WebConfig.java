package com.hhp.ConcertReservation.common.config;

import com.hhp.ConcertReservation.common.intorceptor.QueueInterceptor;
import com.hhp.ConcertReservation.domain.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final QueueService queueService;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new QueueInterceptor(queueService))
				.addPathPatterns("/**")
				.excludePathPatterns("/api/queue");
	}
}
