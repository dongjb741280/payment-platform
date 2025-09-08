package com.payment.engine.service;

import java.math.BigDecimal;

public interface AccountingService {
    boolean book(String paymentMainNo, String paymentDetailNo, BigDecimal amount, String currency);
}


