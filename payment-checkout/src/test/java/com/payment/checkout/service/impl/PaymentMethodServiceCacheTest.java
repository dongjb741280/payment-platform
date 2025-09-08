package com.payment.checkout.service.impl;

import com.payment.checkout.dto.PaymentMethodDTO;
import com.payment.checkout.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentMethodServiceCacheTest {

    private PaymentMethodServiceImpl service;
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        service = new PaymentMethodServiceImpl();
        cacheService = mock(CacheService.class);
        setField(service, "cacheService", cacheService);
        setField(service, "acquiringClient", null);
        setField(service, "riskService", null);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void consult_shouldReturnFromCache_whenHit() {
        List<PaymentMethodDTO> cached = Arrays.asList(new PaymentMethodDTO("ALIPAY", "ALIPAY", 1, true));
        when(cacheService.get(anyString(), eq(List.class))).thenReturn((List) cached);

        List<PaymentMethodDTO> list = service.consult("T1");
        assertEquals(1, list.size());
        verify(cacheService, times(1)).get(anyString(), eq(List.class));
    }

    @Test
    void consult_shouldComputeAndSetCache_whenMiss() {
        when(cacheService.get(anyString(), eq(List.class))).thenReturn(null);

        List<PaymentMethodDTO> list = service.consult("T1");
        assertNotNull(list);
        assertTrue(list.size() >= 4);
        verify(cacheService, times(1)).set(anyString(), any(List.class), anyLong());
    }
}


