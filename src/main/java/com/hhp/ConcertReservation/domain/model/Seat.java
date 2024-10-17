package com.hhp.ConcertReservation.domain.model;

import com.hhp.ConcertReservation.common.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "seat")
@Data
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "concert_schedule_id", nullable = false)
	private Long concertScheduleId;

	@Column(name = "seat_number", nullable = false)
	private Integer seatNumber;

	@Column(name = "status", length = 100, nullable = false)
	private String status;  // AVAILABLE, RESERVED, PAID

	@Column(name = "price", nullable = false)
	private Long price;

	/**
	 * 좌석의 상태를 확인하여 예약 가능한지 검증하는 메서드
	 * @throws IllegalStateException 좌석이 이미 예약되었거나 결제된 경우
	 */
	public void validateSeatAvailability() {
		if (this.status.equals(SeatStatus.RESERVED.toString()) || this.status.equals(SeatStatus.PAID.toString())) {
			throw new IllegalStateException("해당 좌석은 이미 예약되었습니다.");
		}
	}
}