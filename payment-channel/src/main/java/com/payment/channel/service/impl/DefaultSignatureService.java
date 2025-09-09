package com.payment.channel.service.impl;

import com.payment.channel.service.SignatureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class DefaultSignatureService implements SignatureService {

    @Override
    public boolean verifyAlipaySignature(Map<String, String> params, String publicKey) {
        try {
            // Simplified Alipay signature verification
            // In production, use proper RSA signature verification with Alipay public key
            String sign = params.get("sign");
            String signType = params.get("sign_type");
            
            if (sign == null || signType == null) {
                return false;
            }
            
            // Remove sign and sign_type from params for verification
            Map<String, String> verifyParams = new TreeMap<>(params);
            verifyParams.remove("sign");
            verifyParams.remove("sign_type");
            
            // Build query string
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : verifyParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    if (queryString.length() > 0) {
                        queryString.append("&");
                    }
                    queryString.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            
            // Mock verification - in production use proper RSA verification
            return "RSA2".equals(signType) && sign != null && !sign.isEmpty();
        } catch (Exception e) {
            log.error("Alipay signature verification failed", e);
            return false;
        }
    }

    @Override
    public boolean verifyWeChatPaySignature(Map<String, String> params, String publicKey) {
        try {
            // Simplified WeChat Pay signature verification
            String sign = params.get("sign");
            if (sign == null) {
                return false;
            }
            
            // Remove sign from params
            Map<String, String> verifyParams = new TreeMap<>(params);
            verifyParams.remove("sign");
            
            // Build query string
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : verifyParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    if (queryString.length() > 0) {
                        queryString.append("&");
                    }
                    queryString.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            queryString.append("&key=").append(publicKey);
            
            // Generate MD5 signature
            String calculatedSign = generateMD5(queryString.toString()).toUpperCase();
            
            return calculatedSign.equals(sign);
        } catch (Exception e) {
            log.error("WeChat Pay signature verification failed", e);
            return false;
        }
    }

    @Override
    public boolean verifyUnionPaySignature(Map<String, String> params, String publicKey) {
        try {
            // Simplified UnionPay signature verification
            String signature = params.get("signature");
            if (signature == null) {
                return false;
            }
            
            // Remove signature from params
            Map<String, String> verifyParams = new TreeMap<>(params);
            verifyParams.remove("signature");
            
            // Build query string
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : verifyParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    if (queryString.length() > 0) {
                        queryString.append("&");
                    }
                    queryString.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            
            // Mock verification - in production use proper RSA verification
            return signature != null && !signature.isEmpty();
        } catch (Exception e) {
            log.error("UnionPay signature verification failed", e);
            return false;
        }
    }

    @Override
    public String generateAlipaySignature(Map<String, String> params, String privateKey) {
        try {
            // Simplified Alipay signature generation
            Map<String, String> signParams = new TreeMap<>(params);
            signParams.remove("sign");
            signParams.remove("sign_type");
            
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : signParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    if (queryString.length() > 0) {
                        queryString.append("&");
                    }
                    queryString.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            
            // Mock signature - in production use proper RSA signature
            return "MOCK_ALIPAY_SIGNATURE";
        } catch (Exception e) {
            log.error("Alipay signature generation failed", e);
            return "";
        }
    }

    @Override
    public String generateWeChatPaySignature(Map<String, String> params, String privateKey) {
        try {
            Map<String, String> signParams = new TreeMap<>(params);
            signParams.remove("sign");
            
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : signParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    if (queryString.length() > 0) {
                        queryString.append("&");
                    }
                    queryString.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            queryString.append("&key=").append(privateKey);
            
            return generateMD5(queryString.toString()).toUpperCase();
        } catch (Exception e) {
            log.error("WeChat Pay signature generation failed", e);
            return "";
        }
    }

    @Override
    public String generateUnionPaySignature(Map<String, String> params, String privateKey) {
        try {
            Map<String, String> signParams = new TreeMap<>(params);
            signParams.remove("signature");
            
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : signParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    if (queryString.length() > 0) {
                        queryString.append("&");
                    }
                    queryString.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            
            // Mock signature - in production use proper RSA signature
            return "MOCK_UNIONPAY_SIGNATURE";
        } catch (Exception e) {
            log.error("UnionPay signature generation failed", e);
            return "";
        }
    }

    private String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("MD5 generation failed", e);
            return "";
        }
    }
}
