package com.payment.engine.service.impl;

import com.payment.common.constant.CommonConstants;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.payment.common.util.BusinessIdGenerator;
import com.payment.engine.entity.PaymentDetail;
import com.payment.engine.entity.PaymentMain;
import com.payment.engine.entity.TradeOrderLite;
import com.payment.engine.mapper.PaymentDetailMapper;
import com.payment.engine.mapper.PaymentMainMapper;
import com.payment.engine.mapper.TradeOrderLiteMapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import com.payment.test.UserFriendlySummary;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, UserFriendlySummary.class})
@SuppressWarnings({"unchecked"})
class PaymentServiceImplTest {

    @Mock
    private PaymentMainMapper paymentMainMapper;
    @Mock
    private PaymentDetailMapper paymentDetailMapper;
    private BusinessIdGenerator businessIdGenerator;
    @Mock
    private TradeOrderLiteMapper tradeOrderLiteMapper;

    private PaymentServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PaymentServiceImpl();
        // Initialize MyBatis-Plus lambda metadata for entities used in LambdaUpdateWrapper
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, com.payment.engine.entity.PaymentMain.class);
        TableInfoHelper.initTableInfo(assistant, com.payment.engine.entity.TradeOrderLite.class);
        TableInfoHelper.initTableInfo(assistant, com.payment.engine.entity.PaymentDetail.class);
        businessIdGenerator = new BusinessIdGenerator() {
            @Override
            public String generatePaymentNo() {
                return "P20240001";
            }
        };
        setField(service, "paymentMainMapper", paymentMainMapper);
        setField(service, "paymentDetailMapper", paymentDetailMapper);
        setField(service, "tradeOrderLiteMapper", tradeOrderLiteMapper);
        setField(service, "businessIdGenerator", businessIdGenerator);
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
    void accept_shouldCreateMainAndDetailAndUpdateOrder() {
        String tradeOrderNo = "T1";
        when(tradeOrderLiteMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(mockOrder());
        when(paymentMainMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        ArgumentCaptor<PaymentMain> mainCaptor = ArgumentCaptor.forClass(PaymentMain.class);
        ArgumentCaptor<PaymentDetail> detailCaptor = ArgumentCaptor.forClass(PaymentDetail.class);

        String paymentMainNo = service.accept(tradeOrderNo, CommonConstants.PAYMENT_METHOD_WECHAT);

        verify(paymentMainMapper).insert(mainCaptor.capture());
        verify(paymentDetailMapper).insert(detailCaptor.capture());
        verify(paymentMainMapper).update(isNull(), any(LambdaUpdateWrapper.class));
        verify(tradeOrderLiteMapper).update(isNull(), any(LambdaUpdateWrapper.class));

        PaymentMain main = mainCaptor.getValue();
        assertEquals("P20240001", main.getPaymentMainNo());
        assertEquals(new BigDecimal("100.00"), main.getAmount());

        PaymentDetail detail = detailCaptor.getValue();
        assertEquals(main.getPaymentMainNo(), detail.getPaymentMainNo());
        assertEquals(CommonConstants.PAYMENT_METHOD_WECHAT, detail.getPaymentMethod());

        assertEquals("P20240001", paymentMainNo);
    }

    @Test
    void accept_shouldReuseExistingMain() {
        String tradeOrderNo = "T2";
        PaymentMain existed = new PaymentMain();
        existed.setPaymentMainNo("P_EXIST");
        when(tradeOrderLiteMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(paymentMainMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existed);

        String no = service.accept(tradeOrderNo, CommonConstants.PAYMENT_METHOD_ALIPAY);

        // main not inserted when exists, but detail should be inserted referencing existed main
        verify(paymentMainMapper, never()).insert(any());
        verify(paymentDetailMapper).insert(any(PaymentDetail.class));
        assertEquals("P_EXIST", no);
    }

    @Test
    void result_shouldReturnStatus() {
        PaymentMain main = new PaymentMain();
        main.setStatus(CommonConstants.TRADE_STATUS_PAYING);
        when(paymentMainMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(main);

        String status = service.result("T3");
        assertEquals(CommonConstants.TRADE_STATUS_PAYING, status);
    }

    private TradeOrderLite mockOrder() {
        TradeOrderLite order = new TradeOrderLite();
        order.setTradeOrderNo("T1");
        order.setAmount(new BigDecimal("100.00"));
        order.setCurrency(CommonConstants.CURRENCY_CNY);
        return order;
    }
}


