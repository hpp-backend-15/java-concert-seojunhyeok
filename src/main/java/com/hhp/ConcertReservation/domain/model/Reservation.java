package com.hhp.ConcertReservation.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Data
public class Reservation {
	public static final int SEAT_HOLD_TIME_MINUTES = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "seat_id", nullable = false)
	private Long seatId;

	@Column(name = "status", length = 100, nullable = false)
	private String status;  // PENDING, CONFIRMED, CANCELLED

	@Column(name = "expiry_at", nullable = false)
	private LocalDateTime expiryAt;

	public Reservation() {}

	public Reservation(Long memberId, Long seatId, String status, LocalDateTime expiryAt) {
		this.memberId = memberId;
		this.seatId = seatId;
		this.status = status;
		this.expiryAt = expiryAt;
	}
}