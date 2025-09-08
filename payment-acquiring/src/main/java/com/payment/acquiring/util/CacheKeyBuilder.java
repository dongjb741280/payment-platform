package com.payment.acquiring.util;

import com.payment.acquiring.config.CacheProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CacheKeyBuilder {
    @Resource
    private CacheProperties cacheProperties;

    public String trade(String tradeOrderNo) {
        return cacheProperties.getPrefix() + "acq:trade:" + tradeOrderNo;
    }

    public String tradeByMerchant(String merchantId, String merchantOrderNo) {
        return cacheProperties.getPrefix() + "acq:trade:merchant:" + merchantId + ":" + merchantOrderNo;
    }
}


