package com.hhp.ConcertReservation.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "concert_schedule")
@Data
public class ConcertSchedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "concert_id", nullable = false)
	private Long concertId;

	@Column(name = "concert_date", nullable = false)
	private LocalDateTime concertDate;
}