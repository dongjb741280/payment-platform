package com.payment.engine.util;

import com.payment.engine.config.CacheProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CacheKeyBuilder {

    @Resource
    private CacheProperties cacheProperties;

    public String tradeLite(String tradeOrderNo) {
        return cacheProperties.getPrefix() + "trade:lite:" + tradeOrderNo;
    }

    public String paymentStatus(String tradeOrderNo) {
        return cacheProperties.getPrefix() + "payment:status:" + tradeOrderNo;
    }
}


