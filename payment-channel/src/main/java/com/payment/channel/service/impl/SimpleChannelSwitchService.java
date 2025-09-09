package com.payment.channel.service.impl;

import com.payment.channel.service.ChannelSwitchService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleChannelSwitchService implements ChannelSwitchService {

    private final Map<String, Boolean> enabledMap = new ConcurrentHashMap<>();

    public void setEnabled(String channelCode, boolean enabled) {
        enabledMap.put(channelCode, enabled);
    }

    @Override
    public boolean isEnabled(String channelCode) {
        return enabledMap.getOrDefault(channelCode, true);
    }
}


