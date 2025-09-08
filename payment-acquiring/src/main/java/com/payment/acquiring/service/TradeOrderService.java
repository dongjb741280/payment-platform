package com.payment.acquiring.service;

import com.payment.acquiring.dto.TradeOrderCreateRequest;
import com.payment.acquiring.entity.TradeOrder;

import com.payment.common.page.PageResponse;

public interface TradeOrderService {
    TradeOrder createOrder(TradeOrderCreateRequest request);
    TradeOrder getByTradeOrderNo(String tradeOrderNo);
    TradeOrder getByMerchantPair(String merchantId, String merchantOrderNo);
    boolean closeOrder(String tradeOrderNo);
    PageResponse<TradeOrder> pageByMerchant(String merchantId, int pageNum, int pageSize);

    // 状态更新
    boolean markPaying(String tradeOrderNo);
    boolean markSuccess(String tradeOrderNo);
    boolean markFailed(String tradeOrderNo);
}
