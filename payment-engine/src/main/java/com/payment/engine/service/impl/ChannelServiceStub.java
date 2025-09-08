package com.payment.engine.service.impl;

import com.payment.engine.service.ChannelService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ChannelServiceStub implements ChannelService {
    @Override
    public ChannelResult pay(String channelCode, String paymentDetailNo, BigDecimal amount, String currency) {
        // 模拟调用渠道：简单成功返回
        return new ChannelResult(true, UUID.randomUUID().toString(), null, null);
    }
}


