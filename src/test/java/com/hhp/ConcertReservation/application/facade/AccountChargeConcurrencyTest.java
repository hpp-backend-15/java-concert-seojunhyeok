package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.domain.service.AccountService;
import com.hhp.ConcertReservation.infra.persistence.AccountJpaRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
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
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class AccountChargeConcurrencyTest {

	@Autowired
	private AccountFacade accountFacade;

	@Autowired
	private AccountService accountService;

	private Long accountId;

	@Autowired
	private AccountJpaRepository accountJpaRepository;

	@BeforeEach
	void setUp() {
		Account account = new Account();
		account.setMemberId(1L);
		account.setBalance(1000L);
		accountId = accountService.save(account).getId();
	}

	@AfterEach
	void tearDown() {
		accountJpaRepository.deleteAll();
	}

	@Test
	@DisplayName("포인트 충전 동시성 테스트 - 낙관적 락")
	void testChargeBalanceConcurrency() throws InterruptedException {
		int threadCount = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger errorCount = new AtomicInteger(0);

		for (int i = 0; i < threadCount; i++) {
			executorService.execute(() -> {
				try {
					accountFacade.chargeBalance(accountId, 100L);
					successCount.incrementAndGet();
				} catch (IllegalStateException e) {
					errorCount.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		Account updatedAccount = accountService.findAccountById(accountId);
		assertEquals(successCount.get() + errorCount.get(), threadCount, "시도 횟수와 성공 실패 횟수가 동일해야 합니다.");
		assertEquals(updatedAccount.getBalance(), 1000L + 100L * successCount.get(), "성공 횟수 만큼 잔액이 충전되어 있어야 합니다.");
	}
}
