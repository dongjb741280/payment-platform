package com.payment.message.controller;

import com.payment.message.model.MessageRequest;
import com.payment.message.model.MessageResponse;
import com.payment.message.model.MessageTemplate;
import com.payment.message.service.MessageConverterService;
import com.payment.message.service.MessageTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/message-gateway")
public class MessageGatewayController {

    private final MessageTemplateService templateService;
    private final MessageConverterService converterService;

    public MessageGatewayController(MessageTemplateService templateService, MessageConverterService converterService) {
        this.templateService = templateService;
        this.converterService = converterService;
    }

    @PostMapping("/convert")
    public ResponseEntity<MessageResponse> convertMessage(@RequestBody MessageRequest request) {
        log.info("Converting message with template: {}", request.getTemplateId());
        MessageResponse response = converterService.convertRequest(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/parse")
    public ResponseEntity<MessageResponse> parseMessage(@RequestBody Map<String, String> request) {
        String rawMessage = request.get("rawMessage");
        String templateId = request.get("templateId");
        
        log.info("Parsing message with template: {}", templateId);
        MessageResponse response = converterService.convertResponse(rawMessage, templateId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/templates")
    public ResponseEntity<MessageTemplate> createTemplate(@RequestBody MessageTemplate template) {
        log.info("Creating template: {}", template.getTemplateName());
        MessageTemplate saved = templateService.saveTemplate(template);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/templates")
    public ResponseEntity<List<MessageTemplate>> getAllTemplates() {
        List<MessageTemplate> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/templates/{templateId}")
    public ResponseEntity<MessageTemplate> getTemplate(@PathVariable String templateId) {
        return templateService.getTemplate(templateId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/templates/channel/{channelCode}")
    public ResponseEntity<List<MessageTemplate>> getTemplatesByChannel(@PathVariable String channelCode) {
        List<MessageTemplate> templates = templateService.getTemplatesByChannel(channelCode);
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/templates/type/{messageType}")
    public ResponseEntity<List<MessageTemplate>> getTemplatesByType(@PathVariable String messageType) {
        List<MessageTemplate> templates = templateService.getTemplatesByType(messageType);
        return ResponseEntity.ok(templates);
    }

    @DeleteMapping("/templates/{templateId}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String templateId) {
        templateService.deleteTemplate(templateId);
        return ResponseEntity.ok().build();
    }
}
