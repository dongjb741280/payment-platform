package com.payment.channel.service.impl;

import com.payment.channel.service.ChannelHealthService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StaticHealthService implements ChannelHealthService {
    private final Map<String, Boolean> healthyMap = new ConcurrentHashMap<>();

    public void setHealthy(String channelCode, boolean healthy) {
        healthyMap.put(channelCode, healthy);
    }

    @Override
    public boolean isHealthy(String channelCode) {
        return healthyMap.getOrDefault(channelCode, true);
    }
}


