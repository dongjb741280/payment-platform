package com.payment.file.service.impl;

import com.payment.file.model.FileInfo;
import com.payment.file.model.FileTransferRequest;
import com.payment.file.model.FileTransferResponse;
import com.payment.file.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.storage.path:/tmp/payment-files}")
    private String storagePath;

    private final Map<String, FileInfo> fileStore = new ConcurrentHashMap<>();

    @Override
    public FileTransferResponse uploadFile(FileTransferRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            MultipartFile file = request.getFile();
            if (file == null || file.isEmpty()) {
                return FileTransferResponse.builder()
                        .success(false)
                        .errorCode("EMPTY_FILE")
                        .errorMessage("File is empty or null")
                        .processingTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }

            String fileId = UUID.randomUUID().toString();
            String fileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(fileName);
            String storedFileName = fileId + fileExtension;
            
            // Create storage directory if not exists
            Path storageDir = Paths.get(storagePath);
            Files.createDirectories(storageDir);
            
            // Save file
            Path filePath = storageDir.resolve(storedFileName);
            file.transferTo(filePath.toFile());

            // Calculate hashes
            String md5Hash = calculateMD5(filePath);
            String sha256Hash = calculateSHA256(filePath);

            // Create file info
            FileInfo fileInfo = FileInfo.builder()
                    .fileId(fileId)
                    .fileName(storedFileName)
                    .originalFileName(fileName)
                    .filePath(filePath.toString())
                    .fileType(request.getFileType())
                    .fileSize(file.getSize())
                    .md5Hash(md5Hash)
                    .sha256Hash(sha256Hash)
                    .uploadTime(LocalDateTime.now())
                    .lastModified(LocalDateTime.now())
                    .encrypted(request.isEncrypt())
                    .encryptionKey(request.getEncryptionKey())
                    .channelCode(request.getChannelCode())
                    .status("UPLOADED")
                    .build();

            fileStore.put(fileId, fileInfo);

            return FileTransferResponse.builder()
                    .success(true)
                    .fileId(fileId)
                    .fileName(fileName)
                    .filePath(filePath.toString())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        } catch (Exception e) {
            log.error("File upload failed", e);
            return FileTransferResponse.builder()
                    .success(false)
                    .errorCode("UPLOAD_ERROR")
                    .errorMessage(e.getMessage())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public FileTransferResponse downloadFile(String fileId) {
        long startTime = System.currentTimeMillis();
        try {
            FileInfo fileInfo = fileStore.get(fileId);
            if (fileInfo == null) {
                return FileTransferResponse.builder()
                        .success(false)
                        .errorCode("FILE_NOT_FOUND")
                        .errorMessage("File not found: " + fileId)
                        .processingTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }

            Path filePath = Paths.get(fileInfo.getFilePath());
            if (!Files.exists(filePath)) {
                return FileTransferResponse.builder()
                        .success(false)
                        .errorCode("FILE_NOT_EXISTS")
                        .errorMessage("File does not exist on disk: " + fileId)
                        .processingTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }

            return FileTransferResponse.builder()
                    .success(true)
                    .fileId(fileId)
                    .fileName(fileInfo.getOriginalFileName())
                    .filePath(fileInfo.getFilePath())
                    .downloadUrl("/api/file-gateway/download/" + fileId)
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        } catch (Exception e) {
            log.error("File download failed", e);
            return FileTransferResponse.builder()
                    .success(false)
                    .errorCode("DOWNLOAD_ERROR")
                    .errorMessage(e.getMessage())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public FileTransferResponse deleteFile(String fileId) {
        long startTime = System.currentTimeMillis();
        try {
            FileInfo fileInfo = fileStore.get(fileId);
            if (fileInfo == null) {
                return FileTransferResponse.builder()
                        .success(false)
                        .errorCode("FILE_NOT_FOUND")
                        .errorMessage("File not found: " + fileId)
                        .processingTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }

            // Delete physical file
            Path filePath = Paths.get(fileInfo.getFilePath());
            Files.deleteIfExists(filePath);

            // Remove from store
            fileStore.remove(fileId);

            return FileTransferResponse.builder()
                    .success(true)
                    .fileId(fileId)
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        } catch (Exception e) {
            log.error("File deletion failed", e);
            return FileTransferResponse.builder()
                    .success(false)
                    .errorCode("DELETE_ERROR")
                    .errorMessage(e.getMessage())
                    .processingTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    @Override
    public FileInfo getFileInfo(String fileId) {
        return fileStore.get(fileId);
    }

    @Override
    public List<FileInfo> listFiles(String channelCode) {
        return fileStore.values().stream()
                .filter(file -> channelCode == null || channelCode.equals(file.getChannelCode()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public InputStream getFileStream(String fileId) throws IOException {
        FileInfo fileInfo = fileStore.get(fileId);
        if (fileInfo == null) {
            throw new FileNotFoundException("File not found: " + fileId);
        }
        return new FileInputStream(fileInfo.getFilePath());
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String calculateMD5(Path filePath) throws IOException {
        // Simplified MD5 calculation - in production use proper hashing
        return "MD5_" + filePath.getFileName().toString();
    }

    private String calculateSHA256(Path filePath) throws IOException {
        // Simplified SHA256 calculation - in production use proper hashing
        return "SHA256_" + filePath.getFileName().toString();
    }
}
