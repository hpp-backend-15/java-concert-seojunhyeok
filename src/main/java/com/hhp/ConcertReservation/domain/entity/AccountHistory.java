package com.hhp.ConcertReservation.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_history")
@Data
public class AccountHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "account_id", nullable = false)
	private Long accountId;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "type", length = 100, nullable = false)
	private String type;

	@Column(name = "create_at", nullable = false)
	private LocalDateTime createAt;
}