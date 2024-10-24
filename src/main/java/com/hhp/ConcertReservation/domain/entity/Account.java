package com.hhp.ConcertReservation.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "account")
@Data
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "balance", nullable = false)
	private Long balance;

	public void chargeBalance(Long amount) {
		if (amount.compareTo(0L) <= 0) {
			throw new IllegalArgumentException("충전금액은 0보다 커야합니다.");
		}

		this.balance += amount;
	}

	public void useBalance(Long amount) {
		if (amount.compareTo(0L) <= 0) {
			throw new IllegalArgumentException("사용금액은 0보다 커야합니다.");
		}

		Long usedBalance = this.balance - amount;

		if (usedBalance.compareTo(0L) < 0) {
			throw new IllegalStateException("잔여 포인트가 부족합니다. 현재 잔액: " + this.balance);
		}

		this.balance = usedBalance;
	}
}
