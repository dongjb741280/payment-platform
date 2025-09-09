package com.payment.channel.handler.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.payment.channel.handler.ChannelHandler;
import com.payment.channel.model.ChannelConfig;
import com.payment.channel.model.payment.ChannelPaymentRequest;
import com.payment.channel.model.payment.ChannelPaymentResponse;
import com.payment.channel.model.query.ChannelQueryRequest;
import com.payment.channel.model.query.ChannelQueryResponse;
import com.payment.channel.model.refund.ChannelRefundRequest;
import com.payment.channel.model.refund.ChannelRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component("alipayChannelHandler")
public class AlipayChannelHandler implements ChannelHandler {

    @Override
    public ChannelPaymentResponse processPayment(ChannelPaymentRequest request, ChannelConfig config) {
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    config.getEndpoint(),
                    config.getAppId(),
                    config.getPrivateKey(),
                    "json",
                    "UTF-8",
                    config.getPublicKey(),
                    "RSA2"
            );

            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(request.getOrderId());
            model.setTotalAmount(request.getAmount().toString());
            model.setSubject(request.getSubject());
            model.setBody(request.getBody());
            model.setProductCode("FAST_INSTANT_TRADE_PAY");

            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setBizModel(model);
            alipayRequest.setReturnUrl(config.getReturnUrl());
            alipayRequest.setNotifyUrl(config.getNotifyUrl());

            AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);
            
            if (response.isSuccess()) {
                return ChannelPaymentResponse.builder()
                        .success(true)
                        .status("PENDING")
                        .channelOrderId(request.getOrderId())
                        .payUrl(response.getBody())
                        .rawResponse(response.getBody())
                        .build();
            } else {
                return ChannelPaymentResponse.builder()
                        .success(false)
                        .errorCode(response.getCode())
                        .errorMessage(response.getMsg())
                        .rawResponse(response.getBody())
                        .build();
            }
        } catch (AlipayApiException e) {
            log.error("Alipay payment error", e);
            return ChannelPaymentResponse.builder()
                    .success(false)
                    .errorCode(e.getErrCode())
                    .errorMessage(e.getErrMsg())
                    .build();
        }
    }

    @Override
    public ChannelRefundResponse processRefund(ChannelRefundRequest request, ChannelConfig config) {
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    config.getEndpoint(),
                    config.getAppId(),
                    config.getPrivateKey(),
                    "json",
                    "UTF-8",
                    config.getPublicKey(),
                    "RSA2"
            );

            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(request.getOrderId());
            model.setRefundAmount(request.getAmount().toString());
            model.setRefundReason(request.getReason());
            model.setOutRequestNo(request.getRefundId());

            AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
            alipayRequest.setBizModel(model);

            AlipayTradeRefundResponse response = alipayClient.execute(alipayRequest);
            
            if (response.isSuccess()) {
                return ChannelRefundResponse.builder()
                        .success(true)
                        .status("SUCCESS")
                        .channelRefundId(response.getTradeNo())
                        .rawResponse(response.getBody())
                        .build();
            } else {
                return ChannelRefundResponse.builder()
                        .success(false)
                        .errorCode(response.getCode())
                        .errorMessage(response.getMsg())
                        .rawResponse(response.getBody())
                        .build();
            }
        } catch (AlipayApiException e) {
            log.error("Alipay refund error", e);
            return ChannelRefundResponse.builder()
                    .success(false)
                    .errorCode(e.getErrCode())
                    .errorMessage(e.getErrMsg())
                    .build();
        }
    }

    @Override
    public ChannelQueryResponse queryPayment(ChannelQueryRequest request, ChannelConfig config) {
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    config.getEndpoint(),
                    config.getAppId(),
                    config.getPrivateKey(),
                    "json",
                    "UTF-8",
                    config.getPublicKey(),
                    "RSA2"
            );

            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(request.getOrderId());

            AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
            alipayRequest.setBizModel(model);

            AlipayTradeQueryResponse response = alipayClient.execute(alipayRequest);
            
            if (response.isSuccess()) {
                String tradeStatus = response.getTradeStatus();
                String status = mapAlipayStatus(tradeStatus);
                
                return ChannelQueryResponse.builder()
                        .success(true)
                        .status(status)
                        .channelOrderId(response.getTradeNo())
                        .rawResponse(response.getBody())
                        .build();
            } else {
                return ChannelQueryResponse.builder()
                        .success(false)
                        .errorCode(response.getCode())
                        .errorMessage(response.getMsg())
                        .rawResponse(response.getBody())
                        .build();
            }
        } catch (AlipayApiException e) {
            log.error("Alipay query error", e);
            return ChannelQueryResponse.builder()
                    .success(false)
                    .errorCode(e.getErrCode())
                    .errorMessage(e.getErrMsg())
                    .build();
        }
    }

    private String mapAlipayStatus(String alipayStatus) {
        switch (alipayStatus) {
            case "WAIT_BUYER_PAY":
                return "PENDING";
            case "TRADE_SUCCESS":
                return "SUCCESS";
            case "TRADE_FINISHED":
                return "SUCCESS";
            case "TRADE_CLOSED":
                return "CLOSED";
            default:
                return "PENDING";
        }
    }
}


