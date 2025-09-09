package com.payment.message.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MessageRequest {
    private String templateId;
    private String channelCode;
    private String messageType;
    private Map<String, Object> data;
    private Map<String, String> headers;
    private String targetUrl;
}
