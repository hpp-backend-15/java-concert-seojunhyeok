package com.hhp.ConcertReservation.domain.entity;

import com.hhp.ConcertReservation.common.enums.QueueStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "queue")
@Data
public class Queue {
	public static final int QUEUE_TOKEN_EXPIRY_TIME = 5;
	public static final int MAX_ALLOWED_QUEUE_PASS = 20;

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

	@Column(name = "expiry_at")
	private LocalDateTime expiryAt;

	public void expireToken() {
		this.setStatus(QueueStatus.EXPIRED.name());
	}
}