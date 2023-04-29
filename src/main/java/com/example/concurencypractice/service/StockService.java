package com.example.concurencypractice.service;

import com.example.concurencypractice.domain.Stock;
import com.example.concurencypractice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    @Transactional
    public synchronized void decrease(final Long id, final Long quantity) {
        Stock stock =
                stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock); // 즉시 DB에 반영
    }
}
