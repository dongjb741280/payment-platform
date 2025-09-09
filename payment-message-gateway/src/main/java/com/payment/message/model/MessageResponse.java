package com.payment.message.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MessageResponse {
    private boolean success;
    private String rawMessage;
    private Map<String, Object> parsedData;
    private String errorCode;
    private String errorMessage;
    private long processingTimeMs;
}
