package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.domain.event.ReservationEventPublisher;
import com.hhp.ConcertReservation.domain.event.ReservationEventListener;
import com.hhp.ConcertReservation.domain.event.ReservationSuccessEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationEventIntegrationTest {

	@Autowired
	private ReservationEventPublisher reservationEventPublisher;

	@Autowired
	private ReservationEventListener reservationEventListener;

	@Test
	void testReservationSuccessEvent() {
		// Given
		ReservationSuccessEvent event = new ReservationSuccessEvent("123", "456");

		// When
		reservationEventPublisher.success(event);

		// Then
		assertThat(reservationEventListener.isEventReceived()).isTrue();
	}
}
