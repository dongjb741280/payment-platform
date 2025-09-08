package com.payment.checkout.service;

import com.payment.checkout.dto.PaymentMethodDTO;

import java.util.List;

public interface PaymentMethodService {
    List<PaymentMethodDTO> consult(String tradeOrderNo);
}


