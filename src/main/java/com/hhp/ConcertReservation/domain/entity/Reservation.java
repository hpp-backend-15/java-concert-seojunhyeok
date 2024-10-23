package com.hhp.ConcertReservation.domain.entity;

import com.hhp.ConcertReservation.common.enums.ReservationStatus;
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
	private String status;

	@Column(name = "expiry_at", nullable = false)
	private LocalDateTime expiryAt;

	public Reservation() {}

	public Reservation(Long memberId, Long seatId, String status, LocalDateTime expiryAt) {
		this.memberId = memberId;
		this.seatId = seatId;
		this.status = status;
		this.expiryAt = expiryAt;
	}

	/**
	 * 예약의 상태를 확인하여 결제 가능한지 검증하는 메서드
	 * @throws IllegalStateException 예약이 이미 결제되었거나 취소된 경우
	 */
	public void validateReservationAvailability() {
		if (this.status.equals(ReservationStatus.PAID.name())) {
			throw new IllegalStateException("해당 예약은 이미 결제되었습니다.");
		} else if (this.status.equals(ReservationStatus.CANCELED.name())) {
			throw new IllegalStateException("해당 예약은 이미 취소되었습니다.");
		}
	}
}