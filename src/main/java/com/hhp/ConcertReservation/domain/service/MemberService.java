package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.domain.entity.Member;
import com.hhp.ConcertReservation.infra.persistence.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {
	final MemberJpaRepository memberJpaRepository;

	public Member registerMember(String name) {
		Member member = new Member();
		member.setName(name);
		return memberJpaRepository.save(member);
	}

	public Member save(Member member) {
		return memberJpaRepository.save(member);
	}

	public Member findMemberById(Long memberId) {
		return memberJpaRepository.findById(memberId)
				       .orElseThrow(() -> new NoSuchElementException("멤버 정보를 찾을 수 없습니다."));
	}
}
