package com.hhp.ConcertReservation.domain.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Getter
@Slf4j
@Component
public class ReservationEventListener {

	private boolean eventReceived = false;

	@EventListener
	public void onReservationSuccess(ReservationSuccessEvent event) {
		log.info("Received ReservationSuccessEvent for reservationId: {}, memberId: {}", event.reservationId(), event.memberId());
		eventReceived = true;
	}
}