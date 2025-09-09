package com.payment.message;

import com.payment.message.model.MessageRequest;
import com.payment.message.model.MessageTemplate;
import com.payment.message.service.MessageConverterService;
import com.payment.message.service.MessageTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MessageGatewayTest {

    @Autowired
    private MessageTemplateService templateService;

    @Autowired
    private MessageConverterService converterService;

    @Test
    void testMessageTemplateAndConversion() {
        // Create a test template
        Map<String, Object> defaultValues = new HashMap<>();
        defaultValues.put("version", "1.0");
        defaultValues.put("encoding", "UTF-8");

        MessageTemplate template = MessageTemplate.builder()
                .templateName("Alipay Payment Request")
                .channelCode("alipay")
                .messageType("REQUEST")
                .format("JSON")
                .templateContent("{\n" +
                        "  \"app_id\": \"${appId}\",\n" +
                        "  \"method\": \"${method}\",\n" +
                        "  \"charset\": \"${encoding}\",\n" +
                        "  \"sign_type\": \"RSA2\",\n" +
                        "  \"timestamp\": \"${timestamp}\",\n" +
                        "  \"version\": \"${version}\",\n" +
                        "  \"biz_content\": {\n" +
                        "    \"out_trade_no\": \"${orderId}\",\n" +
                        "    \"total_amount\": \"${amount}\",\n" +
                        "    \"subject\": \"${subject}\"\n" +
                        "  }\n" +
                        "}")
                .defaultValues(defaultValues)
                .enabled(true)
                .build();

        // Save template
        MessageTemplate saved = templateService.saveTemplate(template);
        assertNotNull(saved.getTemplateId());

        // Create message request
        Map<String, Object> data = new HashMap<>();
        data.put("appId", "test_app_id");
        data.put("method", "alipay.trade.page.pay");
        data.put("timestamp", "2024-01-01 12:00:00");
        data.put("orderId", "ORDER123");
        data.put("amount", "100.00");
        data.put("subject", "Test Payment");

        MessageRequest request = MessageRequest.builder()
                .templateId(saved.getTemplateId())
                .channelCode("alipay")
                .messageType("REQUEST")
                .data(data)
                .build();

        // Convert message
        var response = converterService.convertRequest(request);
        assertTrue(response.isSuccess());
        assertNotNull(response.getRawMessage());
        assertTrue(response.getRawMessage().contains("ORDER123"));
        assertTrue(response.getRawMessage().contains("100.00"));
    }
}
