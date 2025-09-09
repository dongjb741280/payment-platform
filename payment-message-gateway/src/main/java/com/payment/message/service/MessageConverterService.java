package com.payment.message.service;

import com.payment.message.model.MessageRequest;
import com.payment.message.model.MessageResponse;

import java.util.Map;

public interface MessageConverterService {
    MessageResponse convertRequest(MessageRequest request);
    MessageResponse convertResponse(String rawResponse, String templateId);
    String generateMessage(MessageRequest request);
    Map<String, Object> parseMessage(String rawMessage, String templateId);
}
