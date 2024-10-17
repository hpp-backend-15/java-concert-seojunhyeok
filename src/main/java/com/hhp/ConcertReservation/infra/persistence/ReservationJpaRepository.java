package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
}
