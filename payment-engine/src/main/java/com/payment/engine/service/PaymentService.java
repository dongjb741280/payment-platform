package com.payment.engine.service;

public interface PaymentService {
    String accept(String tradeOrderNo, String paymentMethod);
    String result(String tradeOrderNo);
}


