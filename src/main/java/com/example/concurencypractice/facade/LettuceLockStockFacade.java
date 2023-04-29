package com.example.concurencypractice.facade;

import com.example.concurencypractice.repository.RedisLockRepository;
import com.example.concurencypractice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade {
    private final RedisLockRepository redisLockRepository;
    private final StockService stockService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(final Long key, final Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(key)) {
            Thread.sleep(100); // spinLock 방식이 redis에게 주는 부하를 줄여주기 위해 sleep

            try{
                stockService.decrease(key, quantity);
            } finally {
                redisLockRepository.unlock(key);
            }
        }
    }
}
