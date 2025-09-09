package com.payment.message.service.impl;

import com.payment.message.model.MessageTemplate;
import com.payment.message.service.MessageTemplateService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class InMemoryMessageTemplateService implements MessageTemplateService {

    private final Map<String, MessageTemplate> templateStore = new ConcurrentHashMap<>();

    @Override
    public MessageTemplate saveTemplate(MessageTemplate template) {
        if (template.getTemplateId() == null) {
            template.setTemplateId(UUID.randomUUID().toString());
        }
        templateStore.put(template.getTemplateId(), template);
        return template;
    }

    @Override
    public Optional<MessageTemplate> getTemplate(String templateId) {
        return Optional.ofNullable(templateStore.get(templateId));
    }

    @Override
    public List<MessageTemplate> getTemplatesByChannel(String channelCode) {
        return templateStore.values().stream()
                .filter(template -> channelCode.equals(template.getChannelCode()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageTemplate> getTemplatesByType(String messageType) {
        return templateStore.values().stream()
                .filter(template -> messageType.equals(template.getMessageType()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTemplate(String templateId) {
        templateStore.remove(templateId);
    }

    @Override
    public List<MessageTemplate> getAllTemplates() {
        return new ArrayList<>(templateStore.values());
    }
}
