package com.hhp.ConcertReservation.domain.event;

public record ReservationSuccessEvent(String reservationId, String memberId) {
}
