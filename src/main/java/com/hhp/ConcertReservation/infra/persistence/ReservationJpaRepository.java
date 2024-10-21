package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
	@Query("SELECT r FROM Reservation r WHERE r.status = :status AND r.expiryAt < :expiryAt")
	List<Reservation> findByStatusAndExpiryAtBefore(@Param("status") String status, @Param("expiryAt") LocalDateTime expiryAt);
}
