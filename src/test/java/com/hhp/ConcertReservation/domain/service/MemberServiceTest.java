package com.hhp.ConcertReservation.domain.service;

import com.hhp.ConcertReservation.domain.entity.Member;
import com.hhp.ConcertReservation.infra.persistence.MemberJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class MemberServiceTest {

	@Mock
	MemberJpaRepository memberJpaRepository;

	@InjectMocks
	MemberService memberService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("멤버 ID로 멤버 조회 성공")
	void findMemberById_success() {
		// Given
		Member member = new Member();
		member.setId(1L);
		when(memberJpaRepository.findById(1L)).thenReturn(Optional.of(member));

		// When
		Member result = memberService.findMemberById(1L);

		// Then
		assertNotNull(result);
		assertEquals(1L, result.getId());
	}

	@Test
	@DisplayName("멤버 ID로 멤버 조회 실패 - 예외 발생")
	void findMemberById_notFound() {
		// Given
		when(memberJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// When & Then
		NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
			memberService.findMemberById(1L);
		});

		assertEquals("멤버 정보를 찾을 수 없습니다.", exception.getMessage());
	}
}
