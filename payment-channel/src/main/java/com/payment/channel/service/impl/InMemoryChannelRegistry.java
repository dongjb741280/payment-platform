package com.payment.channel.service.impl;

import com.payment.channel.handler.ChannelHandler;
import com.payment.channel.model.ChannelConfig;
import com.payment.channel.service.ChannelRegistry;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryChannelRegistry implements ChannelRegistry {

    private final Map<String, ChannelConfig> channelCodeToConfig = new ConcurrentHashMap<>();
    private final Map<String, ChannelHandler> channelCodeToHandler = new ConcurrentHashMap<>();

    @Override
    public void register(String channelCode, ChannelConfig config, ChannelHandler handler) {
        channelCodeToConfig.put(channelCode, config);
        channelCodeToHandler.put(channelCode, handler);
    }

    @Override
    public ChannelHandler getHandler(String channelCode) {
        return channelCodeToHandler.get(channelCode);
    }

    @Override
    public ChannelConfig getConfig(String channelCode) {
        return channelCodeToConfig.get(channelCode);
    }

    @Override
    public Collection<String> listChannelCodes() {
        return channelCodeToConfig.keySet();
    }
}


