package com.example.concurencypractice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long quantity;
    @Version // 낙관적 잠금
    private Long version;

    public Stock(final Long id, final Long quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public void decrease(final Long quantity) {
        if(this.quantity - quantity < 0) {
            throw new RuntimeException(" 재고 부족");
        }
        this.quantity -= quantity;
    }
}
