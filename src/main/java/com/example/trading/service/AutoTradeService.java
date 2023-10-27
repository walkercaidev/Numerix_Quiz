package com.example.trading.service;

import com.example.trading.vo.Price;
import com.example.trading.vo.Trade;
import com.example.trading.vo.TradeDirection;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@EqualsAndHashCode
@Slf4j
@Service
public class AutoTradeService implements TradingAlgorithm {
    private static final int tradeQuantity = 1000;
    private static final int averageCount = 4;
    private static Set<String> validProducts;
    private static Set<String> existProducts = new HashSet<>();
    private static final Lock lock = new ReentrantLock();
    private static final String[] validProductsStr = {"C UN", "AAPL UW", "AAL UW"};
    private static Map<String, Stack<BigDecimal>> records = new HashMap<>();

    public AutoTradeService(){
        validProducts = new HashSet<>(Arrays.asList(validProductsStr));
    }

    @Override
    public synchronized Trade buildTrades(Price price) {
        long startTime = System.currentTimeMillis();
        String currentProduct = price.getProductName();
        BigDecimal currentPrice = price.getPrice();

        // 檢查是否可交易
        if(!validProducts.contains(Objects.requireNonNull(currentProduct))){
            return null;
        }
        try {

            // 已經有此productName
            if(existProducts.contains(Objects.requireNonNull(currentProduct))){
                Stack<BigDecimal> priceStack = records.get(currentProduct);

                // product已有3筆
                if (records.containsKey(currentProduct) && (records.get(currentProduct).size() == averageCount - 1)) {

                    priceStack.add(currentPrice);
                    records.put(currentProduct, priceStack);

                    BigDecimal averagePrice = this.calculateAverage(priceStack);
                    BigDecimal newestPrice = priceStack.peek();

                    // 符合spec，進行交易
                    if (this.isUpwardTrend(averagePrice, newestPrice)) {
                        return this.goTradeProduct(currentProduct, newestPrice);
                    }

                // 未滿3筆
                } else {
                    priceStack.add(currentPrice);
                    records.put(currentProduct, priceStack);
                }

            // product第一次出現
            }else{
                existProducts.add(currentProduct);
                Stack<BigDecimal> stack = new Stack<>();
                stack.add(currentPrice);
                records.put(currentProduct, stack);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("executionTime(MilliSecond): {}", endTime - startTime);
        }
        return null;
    }

    @Transactional
    private Trade goTradeProduct( String productName, BigDecimal newestPrice) {

        // 更新trade product price record
        Stack<BigDecimal> stack = new Stack<>(){{add(newestPrice);}};
        records.put(productName, stack);

        return new Trade(productName, TradeDirection.BUY.name(), newestPrice, tradeQuantity);

    }

    private BigDecimal calculateAverage(Stack<BigDecimal> param) {

        BigDecimal sum = param.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = sum.divide(BigDecimal.valueOf(param.size()));

        return average;
    }

    private boolean isUpwardTrend(BigDecimal averagePrice, BigDecimal newestPrice) {
        return newestPrice.compareTo(averagePrice) > 0 ;
    }
}
