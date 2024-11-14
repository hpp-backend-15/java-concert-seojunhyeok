package com.hhp.ConcertReservation.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;

	public void success(ReservationSuccessEvent reservationSuccessEvent) {
		applicationEventPublisher.publishEvent(reservationSuccessEvent);
	}
}
