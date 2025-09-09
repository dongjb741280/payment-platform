package com.payment.channel.config;

import com.payment.channel.model.ChannelConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "payment.channel")
public class ChannelProperties {
    private Map<String, ChannelConfig> configs = new HashMap<>();
}


