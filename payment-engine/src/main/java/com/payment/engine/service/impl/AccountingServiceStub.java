package com.payment.engine.service.impl;

import com.payment.engine.service.AccountingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountingServiceStub implements AccountingService {
    @Override
    public boolean book(String paymentMainNo, String paymentDetailNo, BigDecimal amount, String currency) {
        // 模拟记账成功
        return true;
    }
}


