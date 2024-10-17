package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.model.Member;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}
