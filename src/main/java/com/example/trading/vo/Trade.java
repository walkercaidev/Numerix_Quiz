package com.example.trading.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class Trade {
    private String productName;
    private String tradeDirection;
    private BigDecimal price;
    private int quantity;
}
