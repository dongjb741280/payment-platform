package com.payment.checkout.util;

import com.payment.checkout.config.CacheProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CacheKeyBuilder {
    @Resource
    private CacheProperties cacheProperties;

    public String order(String tradeOrderNo) {
        return cacheProperties.getPrefix() + "chk:order:" + tradeOrderNo;
    }

    public String consult(String tradeOrderNo) {
        return cacheProperties.getPrefix() + "chk:consult:" + tradeOrderNo;
    }

    public String paymentStatus(String tradeOrderNo) {
        return cacheProperties.getPrefix() + "chk:payment:status:" + tradeOrderNo;
    }
}


