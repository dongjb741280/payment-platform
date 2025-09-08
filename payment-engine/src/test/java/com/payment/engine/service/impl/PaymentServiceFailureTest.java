package com.payment.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.mockito.ArgumentMatchers;
import com.payment.common.constant.CommonConstants;
import com.payment.common.statemachine.StateMachine;
import com.payment.common.util.BusinessIdGenerator;
import com.payment.engine.domain.PaymentEvents;
import com.payment.engine.domain.PaymentStatus;
import com.payment.engine.entity.PaymentDetail;
import com.payment.engine.entity.PaymentMain;
import com.payment.engine.entity.TradeOrderLite;
import com.payment.engine.mapper.PaymentDetailMapper;
import com.payment.engine.mapper.PaymentMainMapper;
import com.payment.engine.mapper.TradeOrderLiteMapper;
import com.payment.engine.service.AccountingService;
import com.payment.engine.service.CacheService;
import com.payment.engine.service.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentServiceFailureTest {

    private PaymentServiceImpl service;
    private PaymentMainMapper paymentMainMapper;
    private PaymentDetailMapper paymentDetailMapper;
    private TradeOrderLiteMapper tradeOrderLiteMapper;
    private BusinessIdGenerator idGen;
    private ChannelService channelService;
    private AccountingService accountingService;
    private StateMachine<PaymentStatus, PaymentEvents> sm;
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        service = new PaymentServiceImpl();
        paymentMainMapper = mock(PaymentMainMapper.class);
        paymentDetailMapper = mock(PaymentDetailMapper.class);
        tradeOrderLiteMapper = mock(TradeOrderLiteMapper.class);
        idGen = new BusinessIdGenerator() {
            @Override
            public String generatePaymentNo() { return "P20240001"; }
            @Override
            public String generateTradeOrderNo() { return "T20240001"; }
            @Override
            public String generateMerchantId() { return "M1"; }
            @Override
            public String generateUserId() { return "U1"; }
            @Override
            public String generateAccountNo() { return "A1"; }
        };
        channelService = mock(ChannelService.class);
        accountingService = mock(AccountingService.class);
        sm = new StateMachine<>();
        sm.accept(PaymentStatus.INIT, PaymentEvents.START_PAY, PaymentStatus.PAYING);
        cacheService = mock(CacheService.class);

        setField(service, "paymentMainMapper", paymentMainMapper);
        setField(service, "paymentDetailMapper", paymentDetailMapper);
        setField(service, "tradeOrderLiteMapper", tradeOrderLiteMapper);
        setField(service, "businessIdGenerator", idGen);
        setField(service, "channelService", channelService);
        setField(service, "accountingService", accountingService);
        setField(service, "paymentStateMachine", sm);
        setField(service, "cacheService", cacheService);

        // idGen is stubbed above
        // real state machine configured above

        TradeOrderLite order = new TradeOrderLite();
        order.setTradeOrderNo("T1");
        order.setAmount(new java.math.BigDecimal("10.00"));
        order.setCurrency(CommonConstants.CURRENCY_CNY);
        order.setStatus(CommonConstants.TRADE_STATUS_INIT);
        when(tradeOrderLiteMapper.selectOne(any())).thenReturn(order);

        PaymentMain main = new PaymentMain();
        main.setPaymentMainNo("PM1");
        main.setTradeOrderNo("T1");
        main.setAmount(new java.math.BigDecimal("10.00"));
        main.setCurrency(CommonConstants.CURRENCY_CNY);
        main.setStatus(PaymentStatus.INIT.getCode());
        when(paymentMainMapper.selectOne(any())).thenReturn(main);
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
    void accept_shouldSetFailed_whenChannelFails() {
        when(channelService.pay(anyString(), anyString(), any(), anyString()))
                .thenReturn(new ChannelService.ChannelResult(false, null, "ERR", "fail"));

        service.accept("T1", CommonConstants.PAYMENT_METHOD_WECHAT);

        verify(paymentDetailMapper, atLeastOnce()).update(isNull(), ArgumentMatchers.<Wrapper<PaymentDetail>>any());
        verify(paymentMainMapper, atLeastOnce()).update(isNull(), ArgumentMatchers.<Wrapper<PaymentMain>>any());
        verify(tradeOrderLiteMapper, atLeastOnce()).update(isNull(), ArgumentMatchers.<Wrapper<TradeOrderLite>>any());
    }

    @Test
    void accept_shouldSetFailed_whenAccountingFails() {
        when(channelService.pay(anyString(), anyString(), any(), anyString()))
                .thenReturn(new ChannelService.ChannelResult(true, "C-001", null, null));
        when(accountingService.book(anyString(), anyString(), any(), anyString())).thenReturn(false);

        service.accept("T1", CommonConstants.PAYMENT_METHOD_WECHAT);

        verify(paymentDetailMapper, atLeastOnce()).update(isNull(), ArgumentMatchers.<Wrapper<PaymentDetail>>any());
        verify(paymentMainMapper, atLeastOnce()).update(isNull(), ArgumentMatchers.<Wrapper<PaymentMain>>any());
        verify(tradeOrderLiteMapper, atLeastOnce()).update(isNull(), ArgumentMatchers.<Wrapper<TradeOrderLite>>any());
    }
}


