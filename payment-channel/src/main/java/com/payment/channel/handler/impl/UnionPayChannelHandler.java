package com.payment.channel.handler.impl;

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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component("unionpayChannelHandler")
public class UnionPayChannelHandler implements ChannelHandler {

    @Override
    public ChannelPaymentResponse processPayment(ChannelPaymentRequest request, ChannelConfig config) {
        try {
            // UnionPay gateway payment
            Map<String, String> params = new HashMap<>();
            params.put("version", "5.1.0");
            params.put("encoding", "UTF-8");
            params.put("certId", "1234567890"); // Certificate ID
            params.put("signMethod", "01"); // RSA
            params.put("txnType", "01"); // Purchase
            params.put("txnSubType", "01"); // Self-service consumption
            params.put("bizType", "000201"); // B2C gateway payment
            params.put("channelType", "07"); // Internet
            params.put("accessType", "0"); // Merchant direct access
            params.put("merId", config.getMerchantId());
            params.put("orderId", request.getOrderId());
            params.put("txnTime", getCurrentTime());
            params.put("txnAmt", String.valueOf(request.getAmount().multiply(new BigDecimal("100")).intValue()));
            params.put("currencyCode", "156"); // CNY
            params.put("frontUrl", config.getReturnUrl());
            params.put("backUrl", config.getNotifyUrl());
            params.put("orderDesc", request.getSubject());

            // Generate signature (simplified)
            String sign = generateUnionPaySign(params, config.getPrivateKey());
            params.put("signature", sign);

            // Send request (simplified)
            String response = sendUnionPayRequest(params, config.getEndpoint() + "/gateway/api/appTransReq.do");
            Map<String, String> responseMap = parseUnionPayResponse(response);
            
            if ("00".equals(responseMap.get("respCode"))) {
                return ChannelPaymentResponse.builder()
                        .success(true)
                        .status("PENDING")
                        .channelOrderId(responseMap.get("queryId"))
                        .payUrl(responseMap.get("html"))
                        .rawResponse(response)
                        .build();
            } else {
                return ChannelPaymentResponse.builder()
                        .success(false)
                        .errorCode(responseMap.get("respCode"))
                        .errorMessage(responseMap.get("respMsg"))
                        .rawResponse(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("UnionPay payment error", e);
            return ChannelPaymentResponse.builder()
                    .success(false)
                    .errorMessage("UnionPay payment failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ChannelRefundResponse processRefund(ChannelRefundRequest request, ChannelConfig config) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("version", "5.1.0");
            params.put("encoding", "UTF-8");
            params.put("certId", "1234567890");
            params.put("signMethod", "01");
            params.put("txnType", "04"); // Refund
            params.put("txnSubType", "00");
            params.put("bizType", "000201");
            params.put("accessType", "0");
            params.put("merId", config.getMerchantId());
            params.put("orderId", request.getRefundId());
            params.put("origQryId", request.getChannelOrderId());
            params.put("txnTime", getCurrentTime());
            params.put("txnAmt", String.valueOf(request.getAmount().multiply(new BigDecimal("100")).intValue()));
            params.put("backUrl", config.getNotifyUrl());

            String sign = generateUnionPaySign(params, config.getPrivateKey());
            params.put("signature", sign);

            String response = sendUnionPayRequest(params, config.getEndpoint() + "/gateway/api/backTransReq.do");
            Map<String, String> responseMap = parseUnionPayResponse(response);
            
            if ("00".equals(responseMap.get("respCode"))) {
                return ChannelRefundResponse.builder()
                        .success(true)
                        .status("SUCCESS")
                        .channelRefundId(responseMap.get("queryId"))
                        .rawResponse(response)
                        .build();
            } else {
                return ChannelRefundResponse.builder()
                        .success(false)
                        .errorCode(responseMap.get("respCode"))
                        .errorMessage(responseMap.get("respMsg"))
                        .rawResponse(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("UnionPay refund error", e);
            return ChannelRefundResponse.builder()
                    .success(false)
                    .errorMessage("UnionPay refund failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ChannelQueryResponse queryPayment(ChannelQueryRequest request, ChannelConfig config) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("version", "5.1.0");
            params.put("encoding", "UTF-8");
            params.put("certId", "1234567890");
            params.put("signMethod", "01");
            params.put("txnType", "00"); // Query
            params.put("txnSubType", "00");
            params.put("bizType", "000000");
            params.put("accessType", "0");
            params.put("merId", config.getMerchantId());
            params.put("orderId", request.getOrderId());
            params.put("txnTime", getCurrentTime());

            String sign = generateUnionPaySign(params, config.getPrivateKey());
            params.put("signature", sign);

            String response = sendUnionPayRequest(params, config.getEndpoint() + "/gateway/api/queryTrans.do");
            Map<String, String> responseMap = parseUnionPayResponse(response);
            
            if ("00".equals(responseMap.get("respCode"))) {
                String origRespCode = responseMap.get("origRespCode");
                String status = mapUnionPayStatus(origRespCode);
                
                return ChannelQueryResponse.builder()
                        .success(true)
                        .status(status)
                        .channelOrderId(responseMap.get("queryId"))
                        .rawResponse(response)
                        .build();
            } else {
                return ChannelQueryResponse.builder()
                        .success(false)
                        .errorCode(responseMap.get("respCode"))
                        .errorMessage(responseMap.get("respMsg"))
                        .rawResponse(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("UnionPay query error", e);
            return ChannelQueryResponse.builder()
                    .success(false)
                    .errorMessage("UnionPay query failed: " + e.getMessage())
                    .build();
        }
    }

    private String generateUnionPaySign(Map<String, String> params, String privateKey) {
        // Simplified signature generation - in production use proper UnionPay SDK
        return "MOCK_UNIONPAY_SIGNATURE";
    }

    private String getCurrentTime() {
        return java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String sendUnionPayRequest(Map<String, String> params, String url) {
        // Simplified HTTP request - in production use proper HTTP client
        return "respCode=00&respMsg=Success&queryId=123456789&html=<html>Mock UnionPay Form</html>";
    }

    private Map<String, String> parseUnionPayResponse(String response) {
        // Simplified response parsing
        Map<String, String> result = new HashMap<>();
        String[] pairs = response.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                result.put(keyValue[0], keyValue[1]);
            }
        }
        return result;
    }

    private String mapUnionPayStatus(String origRespCode) {
        switch (origRespCode) {
            case "00":
                return "SUCCESS";
            case "01":
                return "PENDING";
            case "02":
                return "PENDING";
            case "03":
                return "FAILED";
            case "04":
                return "CLOSED";
            default:
                return "PENDING";
        }
    }
}


