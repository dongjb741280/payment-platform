package com.payment.file.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FileInfo {
    private String fileId;
    private String fileName;
    private String originalFileName;
    private String filePath;
    private String fileType;
    private long fileSize;
    private String md5Hash;
    private String sha256Hash;
    private LocalDateTime uploadTime;
    private LocalDateTime lastModified;
    private boolean encrypted;
    private String encryptionKey;
    private String channelCode;
    private String status; // UPLOADED, PROCESSING, COMPLETED, FAILED
}
