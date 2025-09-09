package com.payment.channel.handler;

import com.payment.channel.model.ChannelConfig;
import com.payment.channel.model.query.ChannelQueryRequest;
import com.payment.channel.model.query.ChannelQueryResponse;
import com.payment.channel.model.refund.ChannelRefundRequest;
import com.payment.channel.model.refund.ChannelRefundResponse;
import com.payment.channel.model.payment.ChannelPaymentRequest;
import com.payment.channel.model.payment.ChannelPaymentResponse;

/**
 * Unified abstraction for payment channel operations.
 */
public interface ChannelHandler {
    ChannelPaymentResponse processPayment(ChannelPaymentRequest request, ChannelConfig config);

    ChannelRefundResponse processRefund(ChannelRefundRequest request, ChannelConfig config);

    ChannelQueryResponse queryPayment(ChannelQueryRequest request, ChannelConfig config);
}


