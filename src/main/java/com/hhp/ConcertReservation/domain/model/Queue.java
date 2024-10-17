package com.hhp.ConcertReservation.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "queue")
@Data
public class Queue {
	public static final int QUEUE_TOKEN_EXPIRY_TIME = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "token", length = 100, nullable = false)
	private String token;

	@Column(name = "status", length = 100, nullable = false)
	private String status;  // WAITING, PASSED, EXPIRED

	@Column(name = "expiry_at", nullable = false)
	private LocalDateTime expiryAt;

	public static LocalDateTime getQueueTokenExpiryTime(LocalDateTime now) {
		return now.plusMinutes(QUEUE_TOKEN_EXPIRY_TIME);
	}
}