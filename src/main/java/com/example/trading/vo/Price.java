package com.example.trading.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Getter
@Setter
public class Price {
    private String productName;
    private BigDecimal price;
}
