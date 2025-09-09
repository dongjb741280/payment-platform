package com.payment.file.controller;

import com.payment.file.model.FileInfo;
import com.payment.file.model.FileTransferRequest;
import com.payment.file.model.FileTransferResponse;
import com.payment.file.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/file-gateway")
public class FileGatewayController {

    private final FileStorageService fileStorageService;

    public FileGatewayController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileTransferResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "channelCode", required = false) String channelCode,
            @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam(value = "encrypt", defaultValue = "false") boolean encrypt) {
        
        log.info("Uploading file: {} for channel: {}", file.getOriginalFilename(), channelCode);
        
        FileTransferRequest request = FileTransferRequest.builder()
                .file(file)
                .channelCode(channelCode)
                .fileType(fileType)
                .encrypt(encrypt)
                .operation("UPLOAD")
                .build();
        
        FileTransferResponse response = fileStorageService.uploadFile(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileId) {
        try {
            log.info("Downloading file: {}", fileId);
            
            FileInfo fileInfo = fileStorageService.getFileInfo(fileId);
            if (fileInfo == null) {
                return ResponseEntity.notFound().build();
            }
            
            InputStream inputStream = fileStorageService.getFileStream(fileId);
            InputStreamResource resource = new InputStreamResource(inputStream);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getOriginalFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileInfo.getFileSize())
                    .body(resource);
        } catch (Exception e) {
            log.error("File download failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<FileTransferResponse> deleteFile(@PathVariable String fileId) {
        log.info("Deleting file: {}", fileId);
        FileTransferResponse response = fileStorageService.deleteFile(fileId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info/{fileId}")
    public ResponseEntity<FileInfo> getFileInfo(@PathVariable String fileId) {
        FileInfo fileInfo = fileStorageService.getFileInfo(fileId);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fileInfo);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(@RequestParam(value = "channelCode", required = false) String channelCode) {
        List<FileInfo> files = fileStorageService.listFiles(channelCode);
        return ResponseEntity.ok(files);
    }
}
