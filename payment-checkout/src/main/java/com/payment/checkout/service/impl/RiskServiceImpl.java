package com.payment.checkout.service.impl;

import com.payment.checkout.service.RiskService;
import org.springframework.stereotype.Service;

@Service
public class RiskServiceImpl implements RiskService {
    @Override
    public boolean passBasicCheck(String tradeOrderNo, String paymentMethod) {
        // 简化：全部放行，可在此加入频控/黑名单等
        return true;
    }
}


