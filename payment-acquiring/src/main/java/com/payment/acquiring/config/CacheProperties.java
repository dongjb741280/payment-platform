package com.payment.acquiring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {
    private String prefix = "cache:";
    private long tradeTtlSeconds = 300;
    private long negativeTtlSeconds = 30;

    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public long getTradeTtlSeconds() { return tradeTtlSeconds; }
    public void setTradeTtlSeconds(long tradeTtlSeconds) { this.tradeTtlSeconds = tradeTtlSeconds; }
    public long getNegativeTtlSeconds() { return negativeTtlSeconds; }
    public void setNegativeTtlSeconds(long negativeTtlSeconds) { this.negativeTtlSeconds = negativeTtlSeconds; }
}


