package com.payment.channel.handler.impl;

import com.payment.channel.model.ChannelConfig;
import com.payment.channel.model.payment.ChannelPaymentRequest;
import com.payment.channel.model.payment.ChannelPaymentResponse;
import com.payment.channel.model.query.ChannelQueryRequest;
import com.payment.channel.model.query.ChannelQueryResponse;
import com.payment.channel.model.refund.ChannelRefundRequest;
import com.payment.channel.model.refund.ChannelRefundResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.payment.test.UserFriendlySummary;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class, UserFriendlySummary.class})
class MockChannelHandlerTest {

    private MockChannelHandler handler;
    private ChannelConfig config;

    @BeforeEach
    void setUp() {
        handler = new MockChannelHandler();
        config = ChannelConfig.builder()
                .channelCode("MOCK")
                .channelName("Mock Channel")
                .enabled(true)
                .build();
    }

    @Test
    void processPayment_shouldReturnSuccessResponse() {
        // Given
        ChannelPaymentRequest request = ChannelPaymentRequest.builder()
                .orderId("T20240001")
                .amount(new BigDecimal("100.00"))
                .subject("Test Payment")
                .body("Test Payment Body")
                .build();

        // When
        ChannelPaymentResponse response = handler.processPayment(request, config);

        // Then
        assertNotNull(response);
        assertEquals("PENDING", response.getStatus()); // Mock handler returns PENDING
        assertNotNull(response.getChannelOrderId());
        assertNotNull(response.getPayUrl());
    }

    @Test
    void processRefund_shouldReturnSuccessResponse() {
        // Given
        ChannelRefundRequest request = ChannelRefundRequest.builder()
                .orderId("T20240001")
                .refundId("R20240001")
                .channelOrderId("CH20240001")
                .amount(new BigDecimal("50.00"))
                .reason("Test Refund")
                .build();

        // When
        ChannelRefundResponse response = handler.processRefund(request, config);

        // Then
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertNotNull(response.getChannelRefundId());
    }

    @Test
    void queryPayment_shouldReturnSuccessResponse() {
        // Given
        ChannelQueryRequest request = ChannelQueryRequest.builder()
                .orderId("T20240001")
                .channelOrderId("CH20240001")
                .build();

        // When
        ChannelQueryResponse response = handler.queryPayment(request, config);

        // Then
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertNotNull(response.getChannelOrderId());
    }

    @Test
    void processPayment_withNullRequest_shouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> 
            handler.processPayment(null, config));
    }

    @Test
    void processPayment_withNullConfig_shouldHandleGracefully() {
        // Given
        ChannelPaymentRequest request = ChannelPaymentRequest.builder()
                .orderId("T20240001")
                .amount(new BigDecimal("100.00"))
                .build();

        // When
        ChannelPaymentResponse response = handler.processPayment(request, null);

        // Then
        assertNotNull(response);
        // Mock handler handles null config gracefully
    }
}