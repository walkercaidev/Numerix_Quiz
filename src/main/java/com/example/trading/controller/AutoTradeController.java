package com.example.trading.controller;

import com.example.trading.service.AutoTradeService;
import com.example.trading.vo.Price;
import com.example.trading.vo.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AutoTradeController {

    @Autowired
    AutoTradeService autoTradeService;

    @PostMapping("/buyUpwardTrend")
    public ResponseEntity<Trade> buyUpwardTrend(@RequestBody Price params) {
        System.out.println("product:" + params.getProductName());
        System.out.println("price:" + params.getPrice());
        Trade trade = autoTradeService.buildTrades(params);
        return new ResponseEntity<>(trade, HttpStatus.OK);
    }
}
