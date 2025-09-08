package com.payment.acquiring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.payment.acquiring.dto.TradeOrderCreateRequest;
import com.payment.acquiring.entity.TradeOrder;
import com.payment.acquiring.mapper.TradeOrderMapper;
import com.payment.acquiring.service.TradeOrderService;
import com.payment.acquiring.domain.TradeOrderStatus;
import com.payment.common.util.BusinessIdGenerator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.payment.common.page.PageResponse;

@Service
public class TradeOrderServiceImpl implements TradeOrderService {

    @Resource
    private TradeOrderMapper tradeOrderMapper;

    @Resource
    private BusinessIdGenerator businessIdGenerator;

    @Override
    public TradeOrder createOrder(TradeOrderCreateRequest request) {
        // 幂等：同一商户+商户订单号，如果已存在且非终态，直接返回
        TradeOrder existed = getByMerchantPair(request.getMerchantId(), request.getMerchantOrderNo());
        if (existed != null) {
            return existed;
        }
        TradeOrder order = new TradeOrder();
        order.setTradeOrderNo(businessIdGenerator.generateTradeOrderNo());
        order.setMerchantId(request.getMerchantId());
        order.setMerchantOrderNo(request.getMerchantOrderNo());
        order.setUserId(request.getUserId());
        order.setAmount(request.getAmount());
        order.setCurrency(request.getCurrency());
        order.setSubject(request.getSubject());
        order.setBody(request.getBody());
        order.setStatus(TradeOrderStatus.INIT.getCode());
        order.setNotifyUrl(request.getNotifyUrl());
        order.setReturnUrl(request.getReturnUrl());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        tradeOrderMapper.insert(order);
        return order;
    }

    @Override
    public TradeOrder getByTradeOrderNo(String tradeOrderNo) {
        return tradeOrderMapper.selectOne(new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getTradeOrderNo, tradeOrderNo));
    }

    @Override
    public TradeOrder getByMerchantPair(String merchantId, String merchantOrderNo) {
        return tradeOrderMapper.selectOne(new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getMerchantId, merchantId)
                .eq(TradeOrder::getMerchantOrderNo, merchantOrderNo));
    }

    @Override
    public boolean closeOrder(String tradeOrderNo) {
        return tradeOrderMapper.update(null, new LambdaUpdateWrapper<TradeOrder>()
                .eq(TradeOrder::getTradeOrderNo, tradeOrderNo)
                .in(TradeOrder::getStatus, TradeOrderStatus.INIT.getCode(), TradeOrderStatus.PAYING.getCode())
                .set(TradeOrder::getStatus, TradeOrderStatus.CLOSED.getCode())
                .set(TradeOrder::getUpdateTime, LocalDateTime.now())) > 0;
    }

    @Override
    public PageResponse<TradeOrder> pageByMerchant(String merchantId, int pageNum, int pageSize) {
        int offset = Math.max(0, (pageNum - 1) * pageSize);
        // total
        Integer total = tradeOrderMapper.selectCount(new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getMerchantId, merchantId)).intValue();
        // records
        List<TradeOrder> records = tradeOrderMapper.selectList(new LambdaQueryWrapper<TradeOrder>()
                .eq(TradeOrder::getMerchantId, merchantId)
                .orderByDesc(TradeOrder::getCreateTime)
                .last("limit " + offset + "," + pageSize));
        return PageResponse.of(total, pageNum, pageSize, records);
    }
}


