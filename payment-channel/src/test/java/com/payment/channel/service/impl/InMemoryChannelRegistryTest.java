package com.payment.channel.service.impl;

import com.payment.channel.handler.ChannelHandler;
import com.payment.channel.handler.impl.MockChannelHandler;
import com.payment.channel.model.ChannelConfig;
import com.payment.channel.service.ChannelRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.payment.test.UserFriendlySummary;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class, UserFriendlySummary.class})
class InMemoryChannelRegistryTest {

    private ChannelRegistry registry;
    private ChannelHandler mockHandler;
    private ChannelConfig config;

    @BeforeEach
    void setUp() {
        registry = new InMemoryChannelRegistry();
        mockHandler = new MockChannelHandler();
        
        config = ChannelConfig.builder()
                .channelCode("MOCK")
                .channelName("Mock Channel")
                .enabled(true)
                .build();
    }

    @Test
    void register_shouldAddChannelSuccessfully() {
        // When
        registry.register("MOCK", config, mockHandler);

        // Then
        assertTrue(registry.listChannelCodes().contains("MOCK"));
        assertEquals(mockHandler, registry.getHandler("MOCK"));
        assertEquals(config, registry.getConfig("MOCK"));
    }

    @Test
    void getHandler_withNonExistentChannel_shouldReturnNull() {
        // When
        ChannelHandler handler = registry.getHandler("NON_EXISTENT");

        // Then
        assertNull(handler);
    }

    @Test
    void getConfig_withNonExistentChannel_shouldReturnNull() {
        // When
        ChannelConfig config = registry.getConfig("NON_EXISTENT");

        // Then
        assertNull(config);
    }

    @Test
    void listChannelCodes_shouldReturnEmptyListWhenNoChannels() {
        // When
        var channelCodes = registry.listChannelCodes();

        // Then
        assertTrue(channelCodes.isEmpty());
    }

    @Test
    void listChannelCodes_shouldReturnAllRegisteredChannels() {
        // Given
        registry.register("MOCK", config, mockHandler);
        
        ChannelConfig config2 = ChannelConfig.builder()
                .channelCode("ALIPAY")
                .channelName("Alipay")
                .enabled(true)
                .build();
        registry.register("ALIPAY", config2, mockHandler);

        // When
        var channelCodes = registry.listChannelCodes();

        // Then
        assertEquals(2, channelCodes.size());
        assertTrue(channelCodes.contains("MOCK"));
        assertTrue(channelCodes.contains("ALIPAY"));
    }

    @Test
    void register_withNullHandler_shouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> 
            registry.register("MOCK", config, null));
    }

    @Test
    void register_withNullConfig_shouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> 
            registry.register("MOCK", null, mockHandler));
    }

    @Test
    void register_withNullChannelCode_shouldThrowException() {
        // When & Then
        assertThrows(NullPointerException.class, () -> 
            registry.register(null, config, mockHandler));
    }
}