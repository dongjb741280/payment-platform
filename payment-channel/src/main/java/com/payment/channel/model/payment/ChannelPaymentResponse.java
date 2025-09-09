package com.payment.channel.model.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChannelPaymentResponse {
    private boolean success;
    private String channelOrderId;
    private String status; // e.g., PENDING, SUCCESS, FAILED
    private String payUrl; // for cashier redirect or QR content
    private String rawResponse;
    private String errorCode;
    private String errorMessage;
}


