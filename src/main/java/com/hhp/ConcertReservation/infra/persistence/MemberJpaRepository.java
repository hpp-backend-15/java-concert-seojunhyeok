package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}
