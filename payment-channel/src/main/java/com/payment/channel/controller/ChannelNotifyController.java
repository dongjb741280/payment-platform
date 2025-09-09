package com.payment.channel.controller;

import com.payment.channel.model.ChannelConfig;
import com.payment.channel.service.ChannelRegistry;
import com.payment.channel.service.SignatureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/notify")
public class ChannelNotifyController {

    private final ChannelRegistry channelRegistry;
    private final SignatureService signatureService;

    public ChannelNotifyController(ChannelRegistry channelRegistry, SignatureService signatureService) {
        this.channelRegistry = channelRegistry;
        this.signatureService = signatureService;
    }

    @PostMapping("/alipay")
    public ResponseEntity<String> alipay(@RequestParam Map<String, String> params) {
        try {
            log.info("Received Alipay notify: {}", params);
            
            ChannelConfig config = channelRegistry.getConfig("alipay");
            if (config == null) {
                log.error("Alipay config not found");
                return ResponseEntity.ok("fail");
            }
            
            // Verify signature
            if (!signatureService.verifyAlipaySignature(params, config.getPublicKey())) {
                log.error("Alipay signature verification failed");
                return ResponseEntity.ok("fail");
            }
            
            // Process notify
            String tradeStatus = params.get("trade_status");
            String outTradeNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            
            log.info("Alipay notify processed: orderId={}, tradeNo={}, status={}", 
                    outTradeNo, tradeNo, tradeStatus);
            
            // TODO: Update order status in database
            // orderService.updateOrderStatus(outTradeNo, mapAlipayStatus(tradeStatus));
            
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Alipay notify processing failed", e);
            return ResponseEntity.ok("fail");
        }
    }

    @PostMapping("/wechat")
    public ResponseEntity<String> wechat(@RequestBody String body) {
        try {
            log.info("Received WeChat Pay notify: {}", body);
            
            ChannelConfig config = channelRegistry.getConfig("wechatpay");
            if (config == null) {
                log.error("WeChat Pay config not found");
                return ResponseEntity.ok("FAIL");
            }
            
            // Parse XML body to Map
            Map<String, String> params = parseXmlToMap(body);
            
            // Verify signature
            if (!signatureService.verifyWeChatPaySignature(params, config.getPrivateKey())) {
                log.error("WeChat Pay signature verification failed");
                return ResponseEntity.ok("FAIL");
            }
            
            // Process notify
            String resultCode = params.get("result_code");
            String outTradeNo = params.get("out_trade_no");
            String transactionId = params.get("transaction_id");
            
            log.info("WeChat Pay notify processed: orderId={}, transactionId={}, resultCode={}", 
                    outTradeNo, transactionId, resultCode);
            
            // TODO: Update order status in database
            // orderService.updateOrderStatus(outTradeNo, mapWeChatPayStatus(resultCode));
            
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            log.error("WeChat Pay notify processing failed", e);
            return ResponseEntity.ok("FAIL");
        }
    }

    @PostMapping("/unionpay")
    public ResponseEntity<String> unionpay(@RequestParam Map<String, String> params) {
        try {
            log.info("Received UnionPay notify: {}", params);
            
            ChannelConfig config = channelRegistry.getConfig("unionpay");
            if (config == null) {
                log.error("UnionPay config not found");
                return ResponseEntity.ok("error");
            }
            
            // Verify signature
            if (!signatureService.verifyUnionPaySignature(params, config.getPublicKey())) {
                log.error("UnionPay signature verification failed");
                return ResponseEntity.ok("error");
            }
            
            // Process notify
            String respCode = params.get("respCode");
            String orderId = params.get("orderId");
            String queryId = params.get("queryId");
            
            log.info("UnionPay notify processed: orderId={}, queryId={}, respCode={}", 
                    orderId, queryId, respCode);
            
            // TODO: Update order status in database
            // orderService.updateOrderStatus(orderId, mapUnionPayStatus(respCode));
            
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            log.error("UnionPay notify processing failed", e);
            return ResponseEntity.ok("error");
        }
    }

    private Map<String, String> parseXmlToMap(String xml) {
        // Simplified XML parsing - in production use proper XML parser
        Map<String, String> result = new HashMap<>();
        // Mock parsing for demo
        result.put("result_code", "SUCCESS");
        result.put("out_trade_no", "ORDER123");
        result.put("transaction_id", "WX123456789");
        return result;
    }
}
