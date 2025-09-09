package com.payment.message.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MessageTemplate {
    private String templateId;
    private String templateName;
    private String channelCode;
    private String messageType; // REQUEST, RESPONSE, NOTIFY
    private String format; // JSON, XML, FORM
    private String templateContent;
    private Map<String, Object> defaultValues;
    private Map<String, String> fieldMappings;
    private boolean enabled;
}
