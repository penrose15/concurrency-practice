package com.example.concurencypractice.facade;

import com.example.concurencypractice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockStockFacade {
    private final RedissonClient redissonClient;
    private final StockService stockService;

    public void decrease(final Long key, final Long quantity) {
        final RLock lock = redissonClient.getLock(key.toString());

        try {
            //획득시도 시간, 락 점유 시간
            if(!lock.tryLock(30, 1, TimeUnit.SECONDS)) {
                System.out.println("redis getlock timeOut");
                return;
            }

            stockService.decrease(key, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
