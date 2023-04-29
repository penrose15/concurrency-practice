package com.example.concurencypractice.facade;

import com.example.concurencypractice.repository.LockRepository;
import com.example.concurencypractice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NamedLockFacade {
    private final LockRepository lockRepository;
    private final StockService stockService;

    @Transactional(propagation = Propagation.REQUIRES_NEW) //부모 트랜잭션과 별도로 실행되어야 한다.
    public void decrease(final Long id, final Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            stockService.decrease(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString()); //Lock 해제
        }
    }
}
