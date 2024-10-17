package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QueueJpaRepository extends JpaRepository<Queue, Long> {
	Optional<Queue> findByToken(@Param("token") String token);

	@Query("SELECT COUNT(q) FROM Queue q WHERE q.status = 'WAITING' AND q.id < (SELECT q2.id FROM Queue q2 WHERE q2.token = :token)")
	Optional<Long> findPositionInWaitingQueue(@Param("token") String token);
}
