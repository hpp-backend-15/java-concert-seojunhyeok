package com.hhp.ConcertReservation.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "concert")
@Data
public class Concert {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "title", length = 100, nullable = false)
	private String title;
}