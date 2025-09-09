package com.payment.channel.model.query;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelQueryResponse {
    private boolean success;
    private String status; // PENDING, SUCCESS, FAILED, CLOSED
    private String channelOrderId;
    private String rawResponse;
    private String errorCode;
    private String errorMessage;
}


