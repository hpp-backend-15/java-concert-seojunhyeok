package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByMemberId(Long memberId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM Account a WHERE a.memberId = :memberId")
	Optional<Account> findByMemberIdWithLock(Long memberId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM Account a WHERE a.id = :accountId")
	Optional<Account> findByIdWithLock(Long accountId);
}
