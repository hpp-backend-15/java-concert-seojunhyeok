package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByMemberId(Long memberId);
}
