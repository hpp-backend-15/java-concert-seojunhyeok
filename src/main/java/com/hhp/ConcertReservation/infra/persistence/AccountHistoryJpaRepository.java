package com.hhp.ConcertReservation.infra.persistence;

import com.hhp.ConcertReservation.domain.model.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHistoryJpaRepository extends JpaRepository<AccountHistory, Long> {
}
