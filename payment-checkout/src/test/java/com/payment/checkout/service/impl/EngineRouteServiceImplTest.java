package com.payment.checkout.service.impl;

import com.payment.common.constant.CommonConstants;

import com.payment.checkout.client.EngineClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.payment.test.UserFriendlySummary;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({UserFriendlySummary.class})
class EngineRouteServiceImplTest {

    private EngineRouteServiceImpl engineRouteService;

    @BeforeEach
    void setUp() {
        engineRouteService = new EngineRouteServiceImpl();
        EngineClient stub = new EngineClient() {
            @Override
            public String accept(String tradeOrderNo, String paymentMethod) {
                return "PM20240001";
            }
        };
        setField(engineRouteService, "engineClient", stub);
    }

    @Test
    void accept_shouldDelegateToEngineClient() {
        String result = engineRouteService.accept("T20240001", CommonConstants.PAYMENT_METHOD_WECHAT);
        assertEquals("PM20240001", result);
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
}


