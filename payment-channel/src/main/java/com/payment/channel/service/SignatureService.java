package com.payment.channel.service;

import java.util.Map;

public interface SignatureService {
    boolean verifyAlipaySignature(Map<String, String> params, String publicKey);
    boolean verifyWeChatPaySignature(Map<String, String> params, String publicKey);
    boolean verifyUnionPaySignature(Map<String, String> params, String publicKey);
    String generateAlipaySignature(Map<String, String> params, String privateKey);
    String generateWeChatPaySignature(Map<String, String> params, String privateKey);
    String generateUnionPaySignature(Map<String, String> params, String privateKey);
}
