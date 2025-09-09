package com.payment.channel.service;

import com.payment.channel.model.payment.ChannelPaymentRequest;

public interface ChannelRouter {
    String selectChannelCode(ChannelPaymentRequest request);
}


