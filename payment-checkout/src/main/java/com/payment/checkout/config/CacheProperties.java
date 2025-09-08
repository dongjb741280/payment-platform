package com.payment.checkout.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {
    private String prefix = "cache:";
    private long orderTtlSeconds = 300;
    private long consultTtlSeconds = 120;
    private long paymentStatusTtlSeconds = 60;
    private long negativeTtlSeconds = 30;

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public long getOrderTtlSeconds() { return orderTtlSeconds; }
    public void setOrderTtlSeconds(long orderTtlSeconds) { this.orderTtlSeconds = orderTtlSeconds; }
    public long getConsultTtlSeconds() { return consultTtlSeconds; }
    public void setConsultTtlSeconds(long consultTtlSeconds) { this.consultTtlSeconds = consultTtlSeconds; }
    public long getPaymentStatusTtlSeconds() { return paymentStatusTtlSeconds; }
    public void setPaymentStatusTtlSeconds(long paymentStatusTtlSeconds) { this.paymentStatusTtlSeconds = paymentStatusTtlSeconds; }
    public long getNegativeTtlSeconds() { return negativeTtlSeconds; }
    public void setNegativeTtlSeconds(long negativeTtlSeconds) { this.negativeTtlSeconds = negativeTtlSeconds; }
}


