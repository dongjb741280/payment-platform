package com.payment.channel.config;

import com.payment.channel.handler.ChannelHandler;
import com.payment.channel.model.ChannelConfig;
import com.payment.channel.service.ChannelRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(ChannelProperties.class)
public class ChannelAutoConfiguration {

    @Bean
    public ChannelBootstrap channelBootstrap(ChannelProperties properties, ChannelRegistry registry, Map<String, ChannelHandler> handlers) {
        return new ChannelBootstrap(properties, registry, handlers);
    }

    public static class ChannelBootstrap {
        private final ChannelProperties properties;
        private final ChannelRegistry registry;
        private final Map<String, ChannelHandler> handlers;

        public ChannelBootstrap(ChannelProperties properties, ChannelRegistry registry, Map<String, ChannelHandler> handlers) {
            this.properties = properties;
            this.registry = registry;
            this.handlers = handlers;
            this.initialize();
        }

        private void initialize() {
            if (properties.getConfigs() == null || properties.getConfigs().isEmpty()) {
                return;
            }
            properties.getConfigs().forEach((code, config) -> {
                ChannelHandler handler = handlers.get(code + "ChannelHandler");
                if (handler != null) {
                    registry.register(code, config, handler);
                }
            });
        }
    }
}


