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

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component("wechatpayChannelHandler")
public class WeChatPayChannelHandler implements ChannelHandler {

    @Override
    public ChannelPaymentResponse processPayment(ChannelPaymentRequest request, ChannelConfig config) {
        try {
            // WeChat Pay Native payment (QR code)
            Map<String, String> params = new HashMap<>();
            params.put("appid", config.getAppId());
            params.put("mch_id", config.getMerchantId());
            params.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
            params.put("body", request.getSubject());
            params.put("out_trade_no", request.getOrderId());
            params.put("total_fee", String.valueOf(request.getAmount().multiply(new BigDecimal("100")).intValue()));
            params.put("spbill_create_ip", request.getClientIp());
            params.put("notify_url", config.getNotifyUrl());
            params.put("trade_type", "NATIVE");
            params.put("product_id", request.getOrderId());

            // Generate signature (simplified - in production use proper WeChat Pay SDK)
            String sign = generateWeChatPaySign(params, config.getPrivateKey());
            params.put("sign", sign);

            // Convert to XML and send request (simplified)
            String xmlRequest = mapToXml(params);
            String response = sendWeChatPayRequest(xmlRequest, config.getEndpoint() + "/pay/unifiedorder");
            
            // Parse response (simplified)
            Map<String, String> responseMap = parseXmlResponse(response);
            
            if ("SUCCESS".equals(responseMap.get("return_code"))) {
                return ChannelPaymentResponse.builder()
                        .success(true)
                        .status("PENDING")
                        .channelOrderId(responseMap.get("prepay_id"))
                        .payUrl(responseMap.get("code_url"))
                        .rawResponse(response)
                        .build();
            } else {
                return ChannelPaymentResponse.builder()
                        .success(false)
                        .errorCode(responseMap.get("return_code"))
                        .errorMessage(responseMap.get("return_msg"))
                        .rawResponse(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("WeChat Pay payment error", e);
            return ChannelPaymentResponse.builder()
                    .success(false)
                    .errorMessage("WeChat Pay payment failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ChannelRefundResponse processRefund(ChannelRefundRequest request, ChannelConfig config) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("appid", config.getAppId());
            params.put("mch_id", config.getMerchantId());
            params.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
            params.put("out_trade_no", request.getOrderId());
            params.put("out_refund_no", request.getRefundId());
            params.put("total_fee", "100"); // Should get from original order
            params.put("refund_fee", String.valueOf(request.getAmount().multiply(new BigDecimal("100")).intValue()));

            String sign = generateWeChatPaySign(params, config.getPrivateKey());
            params.put("sign", sign);

            String xmlRequest = mapToXml(params);
            String response = sendWeChatPayRequest(xmlRequest, config.getEndpoint() + "/secapi/pay/refund");
            
            Map<String, String> responseMap = parseXmlResponse(response);
            
            if ("SUCCESS".equals(responseMap.get("return_code"))) {
                return ChannelRefundResponse.builder()
                        .success(true)
                        .status("SUCCESS")
                        .channelRefundId(responseMap.get("refund_id"))
                        .rawResponse(response)
                        .build();
            } else {
                return ChannelRefundResponse.builder()
                        .success(false)
                        .errorCode(responseMap.get("return_code"))
                        .errorMessage(responseMap.get("return_msg"))
                        .rawResponse(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("WeChat Pay refund error", e);
            return ChannelRefundResponse.builder()
                    .success(false)
                    .errorMessage("WeChat Pay refund failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ChannelQueryResponse queryPayment(ChannelQueryRequest request, ChannelConfig config) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("appid", config.getAppId());
            params.put("mch_id", config.getMerchantId());
            params.put("out_trade_no", request.getOrderId());
            params.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));

            String sign = generateWeChatPaySign(params, config.getPrivateKey());
            params.put("sign", sign);

            String xmlRequest = mapToXml(params);
            String response = sendWeChatPayRequest(xmlRequest, config.getEndpoint() + "/pay/orderquery");
            
            Map<String, String> responseMap = parseXmlResponse(response);
            
            if ("SUCCESS".equals(responseMap.get("return_code"))) {
                String tradeState = responseMap.get("trade_state");
                String status = mapWeChatPayStatus(tradeState);
                
                return ChannelQueryResponse.builder()
                        .success(true)
                        .status(status)
                        .channelOrderId(responseMap.get("transaction_id"))
                        .rawResponse(response)
                        .build();
            } else {
                return ChannelQueryResponse.builder()
                        .success(false)
                        .errorCode(responseMap.get("return_code"))
                        .errorMessage(responseMap.get("return_msg"))
                        .rawResponse(response)
                        .build();
            }
        } catch (Exception e) {
            log.error("WeChat Pay query error", e);
            return ChannelQueryResponse.builder()
                    .success(false)
                    .errorMessage("WeChat Pay query failed: " + e.getMessage())
                    .build();
        }
    }

    private String generateWeChatPaySign(Map<String, String> params, String privateKey) {
        // Simplified signature generation - in production use proper WeChat Pay SDK
        return "MOCK_SIGNATURE";
    }

    private String mapToXml(Map<String, String> params) {
        StringBuilder xml = new StringBuilder("<xml>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            xml.append("<").append(entry.getKey()).append(">")
               .append(entry.getValue())
               .append("</").append(entry.getKey()).append(">");
        }
        xml.append("</xml>");
        return xml.toString();
    }

    private String sendWeChatPayRequest(String xmlRequest, String url) throws IOException {
        // Simplified HTTP request - in production use proper HTTP client
        return "<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg><prepay_id>wx123456789</prepay_id><code_url>weixin://wxpay/bizpayurl?pr=abc123</code_url></xml>";
    }

    private Map<String, String> parseXmlResponse(String xmlResponse) {
        // Simplified XML parsing - in production use proper XML parser
        Map<String, String> result = new HashMap<>();
        result.put("return_code", "SUCCESS");
        result.put("return_msg", "OK");
        result.put("prepay_id", "wx123456789");
        result.put("code_url", "weixin://wxpay/bizpayurl?pr=abc123");
        return result;
    }

    private String mapWeChatPayStatus(String wechatStatus) {
        switch (wechatStatus) {
            case "NOTPAY":
                return "PENDING";
            case "SUCCESS":
                return "SUCCESS";
            case "CLOSED":
                return "CLOSED";
            case "REVOKED":
                return "CLOSED";
            case "USERPAYING":
                return "PENDING";
            case "PAYERROR":
                return "FAILED";
            default:
                return "PENDING";
        }
    }
}


