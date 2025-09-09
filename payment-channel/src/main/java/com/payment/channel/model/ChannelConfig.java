package com.payment.channel.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelConfig {
    private String channelCode;
    private String channelName;
    private boolean enabled;
    private String endpoint;
    private String merchantId;
    private String appId;
    private String publicKey;
    private String privateKey;
    private String notifyUrl;
    private String returnUrl;
}


