package com.payment.file.service;

import com.payment.file.model.FileTransferRequest;
import com.payment.file.model.FileTransferResponse;

public interface FileFormatConverterService {
    FileTransferResponse convertFile(FileTransferRequest request);
    boolean supportsConversion(String fromFormat, String toFormat);
    String[] getSupportedFormats();
}
