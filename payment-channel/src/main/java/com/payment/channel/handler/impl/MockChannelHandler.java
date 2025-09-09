package com.payment.channel.handler.impl;

import com.payment.channel.handler.ChannelHandler;
import com.payment.channel.model.ChannelConfig;
import com.payment.channel.model.payment.ChannelPaymentRequest;
import com.payment.channel.model.payment.ChannelPaymentResponse;
import com.payment.channel.model.query.ChannelQueryRequest;
import com.payment.channel.model.query.ChannelQueryResponse;
import com.payment.channel.model.refund.ChannelRefundRequest;
import com.payment.channel.model.refund.ChannelRefundResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("mockChannelHandler")
public class MockChannelHandler implements ChannelHandler {
    @Override
    public ChannelPaymentResponse processPayment(ChannelPaymentRequest request, ChannelConfig config) {
        return ChannelPaymentResponse.builder()
                .success(true)
                .status("PENDING")
                .channelOrderId(UUID.randomUUID().toString())
                .payUrl("https://mock.pay/" + request.getOrderId())
                .rawResponse("MOCK_OK")
                .build();
    }

    @Override
    public ChannelRefundResponse processRefund(ChannelRefundRequest request, ChannelConfig config) {
        return ChannelRefundResponse.builder()
                .success(true)
                .status("SUCCESS")
                .channelRefundId(UUID.randomUUID().toString())
                .rawResponse("MOCK_REFUND_OK")
                .build();
    }

    @Override
    public ChannelQueryResponse queryPayment(ChannelQueryRequest request, ChannelConfig config) {
        return ChannelQueryResponse.builder()
                .success(true)
                .status("SUCCESS")
                .channelOrderId(request.getChannelOrderId())
                .rawResponse("MOCK_QUERY_OK")
                .build();
    }
}


