package com.payment.channel.service.impl;

import com.payment.channel.service.ChannelRouter;
import com.payment.channel.service.ChannelSwitchService;
import com.payment.channel.service.ChannelRegistry;
import com.payment.channel.model.payment.ChannelPaymentRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BasicChannelRouter implements ChannelRouter {

    private final ChannelRegistry registry;
    private final ChannelSwitchService switchService;

    public BasicChannelRouter(ChannelRegistry registry, ChannelSwitchService switchService) {
        this.registry = registry;
        this.switchService = switchService;
    }

    @Override
    public String selectChannelCode(ChannelPaymentRequest request) {
        // Very basic strategy: pick the first enabled channel
        return registry.listChannelCodes().stream()
                .filter(switchService::isEnabled)
                .findFirst()
                .orElse(null);
    }
}


