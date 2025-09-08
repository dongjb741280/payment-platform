package com.payment.engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {
    private String prefix = "cache:";
    private long tradeLiteTtlSeconds = 300;
    private long paymentStatusSuccessTtlSeconds = 300;
    private long paymentStatusFailTtlSeconds = 60;
    private long negativeTtlSeconds = 30;

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public long getTradeLiteTtlSeconds() { return tradeLiteTtlSeconds; }
    public void setTradeLiteTtlSeconds(long tradeLiteTtlSeconds) { this.tradeLiteTtlSeconds = tradeLiteTtlSeconds; }
    public long getPaymentStatusSuccessTtlSeconds() { return paymentStatusSuccessTtlSeconds; }
    public void setPaymentStatusSuccessTtlSeconds(long paymentStatusSuccessTtlSeconds) { this.paymentStatusSuccessTtlSeconds = paymentStatusSuccessTtlSeconds; }
    public long getPaymentStatusFailTtlSeconds() { return paymentStatusFailTtlSeconds; }
    public void setPaymentStatusFailTtlSeconds(long paymentStatusFailTtlSeconds) { this.paymentStatusFailTtlSeconds = paymentStatusFailTtlSeconds; }
    public long getNegativeTtlSeconds() { return negativeTtlSeconds; }
    public void setNegativeTtlSeconds(long negativeTtlSeconds) { this.negativeTtlSeconds = negativeTtlSeconds; }
}


