package com.payment.file.service;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileEncryptionService {
    void encryptFile(InputStream inputStream, OutputStream outputStream, String key);
    void decryptFile(InputStream inputStream, OutputStream outputStream, String key);
    String generateKey();
    boolean validateKey(String key);
}
