package com.payment.channel.model.payment;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ChannelPaymentRequest {
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String subject;
    private String body;
    private String clientIp;
}


