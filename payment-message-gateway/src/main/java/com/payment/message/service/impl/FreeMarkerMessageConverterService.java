package com.payment.message.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payment.message.model.MessageRequest;
import com.payment.message.model.MessageResponse;
import com.payment.message.model.MessageTemplate;
import com.payment.message.service.MessageConverterService;
import com.payment.message.service.MessageTemplateService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class FreeMarkerMessageConverterService implements MessageConverterService {

    private final MessageTemplateService templateService;
    private final Configuration freemarkerConfig;
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    public FreeMarkerMessageConverterService(MessageTemplateService templateService) {
        this.templateService = templateService;
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
        this.jsonMapper = new ObjectMapper();
        this.xmlMapper = new XmlMapper();
    }

    @Override
    public MessageResponse convertRequest(MessageRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            Optional<MessageTemplate> templateOpt = templateService.getTemplate(request.getTemplateId());
            if (templateOpt.isEmpty()) {
                return MessageResponse.builder()
                        .success(false)
                        .errorCode("TEMPLATE_NOT_FOUND")
                        .errorMessage("Template not found: " + request.getTemplateId())
                        .processingTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }

            MessageTemplate template = templateOpt.get();
            String generatedMessage = generateMessage(request);
            
            return MessageResponse.builder()
                    .success(true)
                    .rawMessage(generatedMessage)
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        } catch (Exception e) {
            log.error("Message conversion failed", e);
            return MessageResponse.builder()
                    .success(false)
                    .errorCode("CONVERSION_ERROR")
                    .errorMessage(e.getMessage())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public MessageResponse convertResponse(String rawResponse, String templateId) {
        long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> parsedData = parseMessage(rawResponse, templateId);
            
            return MessageResponse.builder()
                    .success(true)
                    .rawMessage(rawResponse)
                    .parsedData(parsedData)
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        } catch (Exception e) {
            log.error("Response parsing failed", e);
            return MessageResponse.builder()
                    .success(false)
                    .errorCode("PARSE_ERROR")
                    .errorMessage(e.getMessage())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public String generateMessage(MessageRequest request) {
        try {
            Optional<MessageTemplate> templateOpt = templateService.getTemplate(request.getTemplateId());
            if (templateOpt.isEmpty()) {
                throw new RuntimeException("Template not found: " + request.getTemplateId());
            }

            MessageTemplate template = templateOpt.get();
            Map<String, Object> dataModel = new HashMap<>();
            
            // Add request data
            if (request.getData() != null) {
                dataModel.putAll(request.getData());
            }
            
            // Add default values from template
            if (template.getDefaultValues() != null) {
                dataModel.putAll(template.getDefaultValues());
            }

            // Apply field mappings
            if (template.getFieldMappings() != null) {
                Map<String, Object> mappedData = new HashMap<>();
                for (Map.Entry<String, String> mapping : template.getFieldMappings().entrySet()) {
                    String sourceField = mapping.getKey();
                    String targetField = mapping.getValue();
                    if (dataModel.containsKey(sourceField)) {
                        mappedData.put(targetField, dataModel.get(sourceField));
                    }
                }
                dataModel.putAll(mappedData);
            }

            // Process template based on format
            String templateContent = template.getTemplateContent();
            String result;
            
            switch (template.getFormat().toUpperCase()) {
                case "JSON":
                    result = processJsonTemplate(templateContent, dataModel);
                    break;
                case "XML":
                    result = processXmlTemplate(templateContent, dataModel);
                    break;
                case "FORM":
                    result = processFormTemplate(templateContent, dataModel);
                    break;
                default:
                    result = processFreeMarkerTemplate(templateContent, dataModel);
            }
            
            return result;
        } catch (Exception e) {
            log.error("Message generation failed", e);
            throw new RuntimeException("Message generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> parseMessage(String rawMessage, String templateId) {
        try {
            Optional<MessageTemplate> templateOpt = templateService.getTemplate(templateId);
            if (templateOpt.isEmpty()) {
                throw new RuntimeException("Template not found: " + templateId);
            }

            MessageTemplate template = templateOpt.get();
            
            switch (template.getFormat().toUpperCase()) {
                case "JSON":
                    return jsonMapper.readValue(rawMessage, Map.class);
                case "XML":
                    return xmlMapper.readValue(rawMessage, Map.class);
                case "FORM":
                    return parseFormData(rawMessage);
                default:
                    return new HashMap<>();
            }
        } catch (Exception e) {
            log.error("Message parsing failed", e);
            throw new RuntimeException("Message parsing failed: " + e.getMessage(), e);
        }
    }

    private String processFreeMarkerTemplate(String templateContent, Map<String, Object> dataModel) throws Exception {
        Template template = new Template("template", new StringReader(templateContent), freemarkerConfig);
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }

    private String processJsonTemplate(String templateContent, Map<String, Object> dataModel) throws Exception {
        Template template = new Template("template", new StringReader(templateContent), freemarkerConfig);
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }

    private String processXmlTemplate(String templateContent, Map<String, Object> dataModel) throws Exception {
        Template template = new Template("template", new StringReader(templateContent), freemarkerConfig);
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }

    private String processFormTemplate(String templateContent, Map<String, Object> dataModel) throws Exception {
        Template template = new Template("template", new StringReader(templateContent), freemarkerConfig);
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }

    private Map<String, Object> parseFormData(String formData) {
        Map<String, Object> result = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                result.put(keyValue[0], keyValue[1]);
            }
        }
        return result;
    }
}
