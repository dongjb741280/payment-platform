package com.payment.acquiring.service.impl;

import com.payment.common.constant.CommonConstants;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.payment.acquiring.dto.TradeOrderCreateRequest;
import com.payment.acquiring.entity.TradeOrder;
import com.payment.acquiring.mapper.TradeOrderMapper;
import com.payment.acquiring.domain.TradeOrderStatus;
import com.payment.common.page.PageResponse;
import com.payment.common.util.BusinessIdGenerator;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import com.payment.test.UserFriendlySummary;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, UserFriendlySummary.class})
@SuppressWarnings({"unchecked"})
class TradeOrderServiceImplTest {

    @Mock
    private TradeOrderMapper tradeOrderMapper;
    private BusinessIdGenerator businessIdGenerator;
    private TradeOrderServiceImpl service;

    @BeforeEach
    void setUp() {
        // Initialize MyBatis-Plus TableInfo cache for lambda wrappers
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        TableInfoHelper.initTableInfo(assistant, TradeOrder.class);
        // real service with stubbed id generator and mocked mapper
        service = new TradeOrderServiceImpl();
        businessIdGenerator = new BusinessIdGenerator() {
            @Override
            public String generateTradeOrderNo() {
                return "T20240001";
            }
        };
        setField(service, "tradeOrderMapper", tradeOrderMapper);
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
    void createOrder_shouldReturnExistingIfDuplicate() {
        TradeOrder existing = new TradeOrder();
        existing.setTradeOrderNo("T20231111");
        when(tradeOrderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

        TradeOrderCreateRequest req = new TradeOrderCreateRequest();
        req.setMerchantId("M1");
        req.setMerchantOrderNo("MO1");

        TradeOrder result = service.createOrder(req);

        assertEquals("T20231111", result.getTradeOrderNo());
        verify(tradeOrderMapper, never()).insert(any());
    }

    @Test
    void createOrder_shouldInsertWhenNotExists() {
        when(tradeOrderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        ArgumentCaptor<TradeOrder> orderCaptor = ArgumentCaptor.forClass(TradeOrder.class);

        TradeOrderCreateRequest req = new TradeOrderCreateRequest();
        req.setMerchantId("M1");
        req.setMerchantOrderNo("MO1");
        req.setUserId("U1");
        req.setAmount(new BigDecimal("12.34"));
        req.setCurrency(CommonConstants.CURRENCY_CNY);
        req.setSubject("S");
        req.setBody("B");
        req.setNotifyUrl("N");
        req.setReturnUrl("R");

        TradeOrder result = service.createOrder(req);

        verify(tradeOrderMapper).insert(orderCaptor.capture());
        TradeOrder inserted = orderCaptor.getValue();
        assertEquals("T20240001", inserted.getTradeOrderNo());
        assertEquals(TradeOrderStatus.INIT.getCode(), inserted.getStatus());
        assertNotNull(inserted.getCreateTime());
        assertNotNull(inserted.getUpdateTime());
        assertEquals(result, inserted);
    }

    @Test
    void getByTradeOrderNo_shouldDelegateToMapper() {
        service.getByTradeOrderNo("T1");
        verify(tradeOrderMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    void getByMerchantPair_shouldDelegateToMapper() {
        service.getByMerchantPair("M1", "MO1");
        verify(tradeOrderMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    void closeOrder_shouldUpdateStatusWhenValid() {
        when(tradeOrderMapper.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(1);
        boolean ok = service.closeOrder("T1");
        assertTrue(ok);
    }

    @Test
    void pageByMerchant_shouldReturnPageResponse() {
        when(tradeOrderMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);
        when(tradeOrderMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.singletonList(new TradeOrder()));

        PageResponse<TradeOrder> resp = service.pageByMerchant("M1", 1, 10);
        assertEquals(5, resp.getTotal());
        assertEquals(1, resp.getPageNum());
        assertEquals(10, resp.getPageSize());
        assertEquals(1, resp.getRecords().size());
    }
}


