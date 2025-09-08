package com.payment.checkout.service.impl;

import com.payment.common.constant.CommonConstants;
import com.payment.checkout.dto.PaymentMethodDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.payment.test.UserFriendlySummary;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(UserFriendlySummary.class)
class PaymentMethodServiceImplTest {

    @Mock
    private MessageSource messageSource;
    
    private PaymentMethodServiceImpl service;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PaymentMethodServiceImpl(messageSource);
        
        // Mock MessageSource responses
        when(messageSource.getMessage("payment.method.balance", null, LocaleContextHolder.getLocale()))
            .thenReturn("余额支付");
        when(messageSource.getMessage("payment.method.wechat", null, LocaleContextHolder.getLocale()))
            .thenReturn("微信支付");
        when(messageSource.getMessage("payment.method.alipay", null, LocaleContextHolder.getLocale()))
            .thenReturn("支付宝");
        when(messageSource.getMessage("payment.method.unionpay", null, LocaleContextHolder.getLocale()))
            .thenReturn("银联");
    }

    @Test
    void consult_shouldReturnSortedListAndContainExpectedMethods() {
        List<PaymentMethodDTO> list = service.consult("T20240001");

        assertNotNull(list);
        assertTrue(list.size() >= 4);
        // sorted by sort asc
        for (int i = 1; i < list.size(); i++) {
            assertTrue(list.get(i - 1).getSort() <= list.get(i).getSort());
        }
        // contains specific codes
        assertTrue(list.stream().anyMatch(pm -> CommonConstants.PAYMENT_METHOD_BALANCE.equals(pm.getCode())));
        assertTrue(list.stream().anyMatch(pm -> CommonConstants.PAYMENT_METHOD_WECHAT.equals(pm.getCode())));
        assertTrue(list.stream().anyMatch(pm -> CommonConstants.PAYMENT_METHOD_ALIPAY.equals(pm.getCode())));
        assertTrue(list.stream().anyMatch(pm -> CommonConstants.PAYMENT_METHOD_UNIONPAY.equals(pm.getCode())));
        
        // Verify MessageSource was called
        verify(messageSource).getMessage("payment.method.balance", null, LocaleContextHolder.getLocale());
        verify(messageSource).getMessage("payment.method.wechat", null, LocaleContextHolder.getLocale());
        verify(messageSource).getMessage("payment.method.alipay", null, LocaleContextHolder.getLocale());
        verify(messageSource).getMessage("payment.method.unionpay", null, LocaleContextHolder.getLocale());
    }
}


