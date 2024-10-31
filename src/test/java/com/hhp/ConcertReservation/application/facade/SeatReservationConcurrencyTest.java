package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.domain.entity.Member;
import com.hhp.ConcertReservation.domain.entity.Seat;
import com.hhp.ConcertReservation.infra.persistence.MemberJpaRepository;
import com.hhp.ConcertReservation.infra.persistence.SeatJpaRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class SeatReservationConcurrencyTest {

	@Autowired
	private ReservationFacade reservationFacade;

	@Autowired
	private SeatJpaRepository seatRepository;

	@Autowired
	private MemberJpaRepository memberRepository;

	private Seat seat;
	private Member member;
	private Long seatId;

	@BeforeEach
	public void setup() {
		// 테스트용 좌석과 멤버를 생성
		seat = new Seat();
		seat.setConcertScheduleId(1L);
		seat.setSeatNumber(1);
		seat.setStatus("AVAILABLE");
		seat.setPrice(5000L);
		seat = seatRepository.save(seat);
		seatId = seat.getId();

		member = new Member();
		member.setName("test");
		member = memberRepository.save(member);
	}

	@Test
	@DisplayName("동시 좌석 예약 테스트 - 낙관적 락 적용")
	public void testConcurrentSeatReservation() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicLong successCount = new AtomicLong(0);
		AtomicLong failureCount = new AtomicLong(0);

		for (int i = 0; i < threadCount; i++) {
			executorService.execute(() -> {
				try {
					reservationFacade.reserveSeat(member.getId(), seatId);
					successCount.incrementAndGet();
				} catch (IllegalStateException e) {
					System.out.println(e.getMessage());
					failureCount.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		// 좌석 상태 확인
		Seat updatedSeat = seatRepository.findById(seatId).orElseThrow();
		assertEquals("RESERVED", updatedSeat.getStatus());

		// 성공한 스레드는 1개여야 함
		System.out.println("successCount = " + successCount.get());
		System.out.println("failureCount = " + failureCount.get());
		assertEquals(1, successCount.get(), "성공한 스레드는 1개여야 합니다.");
		assertEquals(threadCount - 1, failureCount.get(), "실패한 스레드는 " + (threadCount - 1) + "개여야 합니다.");


		executorService.shutdown();
	}
}
