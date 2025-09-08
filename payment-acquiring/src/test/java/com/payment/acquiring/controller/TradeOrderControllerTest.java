package com.payment.acquiring.controller;

import com.payment.acquiring.dto.TradeOrderCreateRequest;
import com.payment.acquiring.entity.TradeOrder;
import com.payment.acquiring.service.TradeOrderService;
import com.payment.common.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import com.payment.test.UserFriendlySummary;
import org.springframework.context.MessageSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, UserFriendlySummary.class})
class TradeOrderControllerTest {

    @Mock
    private TradeOrderService tradeOrderService;
    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private TradeOrderController controller;

    @Test
    void create_shouldReturnSuccess() {
        TradeOrder order = new TradeOrder();
        when(tradeOrderService.createOrder(any())).thenReturn(order);
        Result<TradeOrder> result = controller.create(new TradeOrderCreateRequest());
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    void get_shouldReturnBusinessErrorWhenNotFound() {
        when(tradeOrderService.getByTradeOrderNo("T1")).thenReturn(null);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("order not exist");
        Result<TradeOrder> result = controller.get("T1");
        assertFalse(result.isSuccess());
        assertEquals("order not exist", result.getMessage());
    }
}


