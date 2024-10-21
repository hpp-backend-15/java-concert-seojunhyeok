package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
