package com.example.concurencypractice.repository;

import com.example.concurencypractice.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
