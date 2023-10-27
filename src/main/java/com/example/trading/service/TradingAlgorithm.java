package com.example.trading.service;

import com.example.trading.vo.Price;
import com.example.trading.vo.Trade;

public interface TradingAlgorithm {
    /**
     *
     */

    /**
     * Builds a trade to be executed based on the supplied prices.
     * @param price data
     * @return trade to execute
     */

    Trade buildTrades(Price price);
}
