package com.payment.channel.model.refund;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ChannelRefundRequest {
    private String orderId;
    private String channelOrderId;
    private String refundId;
    private BigDecimal amount;
    private String reason;
}


