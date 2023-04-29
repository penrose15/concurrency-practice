package com.example.concurencypractice;

import com.example.concurencypractice.domain.Stock;
import com.example.concurencypractice.facade.NamedLockFacade;
import com.example.concurencypractice.repository.LockRepository;
import com.example.concurencypractice.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class NamedLockFacadeTest {

    @Autowired
    private NamedLockFacade namedLockFacade;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private LockRepository lockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    @DisplayName("Pessimistic LOCK 동시에 100개의 요청")
    public void Pessimistic_request_100_AtTheSameTime() {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i = 0; i<threadCount; i++) {
            executorService.submit(() -> {
                try {
                    namedLockFacade.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
            Stock stock = stockRepository.findById(1L).orElseThrow();

            assertThat(stock.getQuantity())
                    .isEqualTo(0L);
        }
    }
}
