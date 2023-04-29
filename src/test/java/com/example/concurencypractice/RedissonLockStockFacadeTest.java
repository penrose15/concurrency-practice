package com.example.concurencypractice;

import com.example.concurencypractice.domain.Stock;
import com.example.concurencypractice.facade.RedissonLockStockFacade;
import com.example.concurencypractice.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedissonLockStockFacadeTest {
    @Autowired
    private RedissonLockStockFacade redissonLockStockFacade;
    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        Stock stock = new Stock(1L, 5L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void requests_100_AtTheSameTime() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);
        IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
                    try {
                        redissonLockStockFacade.decrease(1L, 1L);
                    } finally {
                        latch.countDown();
                    }
                }
        ));

            latch.await();
            Stock stock = stockRepository.findById(1L).orElseThrow();

            assertThat(stock.getQuantity()).isEqualTo(0L);


    }
}
