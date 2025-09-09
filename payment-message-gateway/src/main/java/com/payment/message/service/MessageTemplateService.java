package com.payment.message.service;

import com.payment.message.model.MessageTemplate;

import java.util.List;
import java.util.Optional;

public interface MessageTemplateService {
    MessageTemplate saveTemplate(MessageTemplate template);
    Optional<MessageTemplate> getTemplate(String templateId);
    List<MessageTemplate> getTemplatesByChannel(String channelCode);
    List<MessageTemplate> getTemplatesByType(String messageType);
    void deleteTemplate(String templateId);
    List<MessageTemplate> getAllTemplates();
}
