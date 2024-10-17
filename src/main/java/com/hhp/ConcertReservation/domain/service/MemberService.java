package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.domain.model.Member;
import com.hhp.ConcertReservation.infra.persistence.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
	final MemberJpaRepository memberJpaRepository;

	public Member findMemberById(Long memberId) {
		return memberJpaRepository.findById(memberId)
				       .orElseThrow(() -> new IllegalArgumentException("멤버 정보를 찾을 수 없습니다."));
	}
}
