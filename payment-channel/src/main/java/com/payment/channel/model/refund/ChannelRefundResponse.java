package com.payment.channel.model.refund;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelRefundResponse {
    private boolean success;
    private String status; // PENDING, SUCCESS, FAILED
    private String channelRefundId;
    private String rawResponse;
    private String errorCode;
    private String errorMessage;
}


