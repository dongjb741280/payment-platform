package com.payment.file;

import com.payment.file.model.FileInfo;
import com.payment.file.model.FileTransferRequest;
import com.payment.file.model.FileTransferResponse;
import com.payment.file.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FileGatewayTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    void testFileUploadAndDownload() {
        // Create a mock file
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello, Payment Platform!".getBytes()
        );

        // Upload file
        FileTransferRequest uploadRequest = FileTransferRequest.builder()
                .file(mockFile)
                .channelCode("test")
                .fileType("TXT")
                .operation("UPLOAD")
                .encrypt(false)
                .build();

        FileTransferResponse uploadResponse = fileStorageService.uploadFile(uploadRequest);
        assertTrue(uploadResponse.isSuccess());
        assertNotNull(uploadResponse.getFileId());

        String fileId = uploadResponse.getFileId();

        // Get file info
        FileInfo fileInfo = fileStorageService.getFileInfo(fileId);
        assertNotNull(fileInfo);
        assertEquals("test.txt", fileInfo.getOriginalFileName());
        assertEquals("test", fileInfo.getChannelCode());

        // Download file
        FileTransferResponse downloadResponse = fileStorageService.downloadFile(fileId);
        assertTrue(downloadResponse.isSuccess());
        assertEquals(fileId, downloadResponse.getFileId());

        // List files
        var files = fileStorageService.listFiles("test");
        assertFalse(files.isEmpty());
        assertTrue(files.stream().anyMatch(f -> f.getFileId().equals(fileId)));

        // Delete file
        FileTransferResponse deleteResponse = fileStorageService.deleteFile(fileId);
        assertTrue(deleteResponse.isSuccess());

        // Verify file is deleted
        FileInfo deletedFileInfo = fileStorageService.getFileInfo(fileId);
        assertNull(deletedFileInfo);
    }
}
