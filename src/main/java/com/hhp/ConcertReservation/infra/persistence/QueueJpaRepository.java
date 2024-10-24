package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.entity.Queue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueJpaRepository extends JpaRepository<Queue, Long> {
	Optional<Queue> findByToken(@Param("token") String token);

	Optional<Queue> findByMemberId(@Param("member_id") Long memberId);

	@Query("SELECT COUNT(q) FROM Queue q WHERE q.status = 'WAITING' AND q.id <= (SELECT q2.id FROM Queue q2 WHERE q2.token = :token)")
	Optional<Long> findPositionInWaitingQueue(@Param("token") String token);

	Optional<Long> countByStatus(@Param("status") String status);

	@Query("SELECT q FROM Queue q WHERE q.status = :status ORDER BY q.id ASC")
	List<Queue> findTopByStatusOrderByIdAsc(@Param("status") String status, Pageable pageable);

	@Query("select q from Queue q WHERE q.status = 'ENTERED' AND q.expiryAt <= :expiryAt")
	List<Queue> findExpireOverdueQueues(@Param("expiryAt") LocalDateTime expiryAt);

}
