package com.payment.channel.service;

import com.payment.channel.handler.ChannelHandler;
import com.payment.channel.model.ChannelConfig;

import java.util.Collection;

public interface ChannelRegistry {
    void register(String channelCode, ChannelConfig config, ChannelHandler handler);
    ChannelHandler getHandler(String channelCode);
    ChannelConfig getConfig(String channelCode);
    Collection<String> listChannelCodes();
}


