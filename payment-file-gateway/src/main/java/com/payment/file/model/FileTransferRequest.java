package com.payment.file.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Data
@Builder
public class FileTransferRequest {
    private String channelCode;
    private String fileType; // EXCEL, CSV, XML, JSON, PDF
    private String operation; // UPLOAD, DOWNLOAD, CONVERT
    private String targetFormat;
    private boolean encrypt;
    private String encryptionKey;
    private Map<String, String> metadata;
    private MultipartFile file;
    private String fileId;
    private String remotePath;
}
