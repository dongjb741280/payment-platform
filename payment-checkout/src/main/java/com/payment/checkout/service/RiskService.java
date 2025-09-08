package com.payment.checkout.service;

public interface RiskService {
    boolean passBasicCheck(String tradeOrderNo, String paymentMethod);
}


