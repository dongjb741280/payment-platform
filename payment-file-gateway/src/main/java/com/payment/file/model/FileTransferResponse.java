package com.payment.file.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class FileTransferResponse {
    private boolean success;
    private String fileId;
    private String fileName;
    private String filePath;
    private String downloadUrl;
    private String errorCode;
    private String errorMessage;
    private Map<String, Object> metadata;
    private long processingTimeMs;
}
