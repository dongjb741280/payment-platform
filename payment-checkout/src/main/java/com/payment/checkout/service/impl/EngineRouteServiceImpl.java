package com.payment.checkout.service.impl;

import com.payment.checkout.client.EngineClient;
import com.payment.checkout.service.EngineRouteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EngineRouteServiceImpl implements EngineRouteService {

    @Resource
    private EngineClient engineClient;

    @Override
    public String accept(String tradeOrderNo, String paymentMethod) {
        return engineClient.accept(tradeOrderNo, paymentMethod);
    }
}


