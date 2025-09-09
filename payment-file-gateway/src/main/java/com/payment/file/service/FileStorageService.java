package com.payment.file.service;

import com.payment.file.model.FileInfo;
import com.payment.file.model.FileTransferRequest;
import com.payment.file.model.FileTransferResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileStorageService {
    FileTransferResponse uploadFile(FileTransferRequest request);
    FileTransferResponse downloadFile(String fileId);
    FileTransferResponse deleteFile(String fileId);
    FileInfo getFileInfo(String fileId);
    List<FileInfo> listFiles(String channelCode);
    InputStream getFileStream(String fileId) throws IOException;
}
