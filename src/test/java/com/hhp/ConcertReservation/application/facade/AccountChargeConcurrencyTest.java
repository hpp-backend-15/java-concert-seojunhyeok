package com.hhp.ConcertReservation.application.facade;

import com.hhp.ConcertReservation.domain.entity.Account;
import com.hhp.ConcertReservation.infra.persistence.AccountHistoryJpaRepository;
import com.hhp.ConcertReservation.infra.persistence.AccountJpaRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase
@SpringBootTest
public class AccountChargeConcurrencyTest {

	@Autowired
	private AccountFacade accountFacade;

	@Autowired
	private AccountJpaRepository accountRepository;

	@Autowired
	private AccountHistoryJpaRepository accountHistoryRepository;

	private Account account;
	private Long memberId;

	@BeforeEach
	public void setup() {
		memberId = 1L;

		account = new Account();
		account.setMemberId(memberId);
		account.setBalance(0L);
		account = accountRepository.save(account);
	}

	@AfterEach
	public void tearDown() {
		accountRepository.deleteAll();
		accountHistoryRepository.deleteAll();
	}

	@Test
	@DisplayName("동시 포인트 충전 테스트 - 비관적 락")
	public void testConcurrentAccountChargingUsingFacade() throws InterruptedException {
		//given
		int threadCount = 1000;
		long chargeAmount = 1000L;
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		//when
		for (int i = 0; i < threadCount; i++) {
			executorService.execute(() -> {
				try {
					accountFacade.chargeBalance(memberId, chargeAmount);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		//then
		List<?> histories = accountHistoryRepository.findByAccountId(account.getId());
		assertEquals(threadCount, histories.size());

		Account updatedAccount = accountRepository.findByMemberId(memberId).orElseThrow(NoSuchElementException::new);
		assertEquals(threadCount * chargeAmount, updatedAccount.getBalance());

		executorService.shutdown();
	}
}
