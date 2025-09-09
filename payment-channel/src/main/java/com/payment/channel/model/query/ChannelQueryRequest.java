package com.payment.channel.model.query;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelQueryRequest {
    private String orderId;
    private String channelOrderId;
}


