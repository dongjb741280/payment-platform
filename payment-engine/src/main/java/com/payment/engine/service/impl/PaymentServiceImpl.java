package com.payment.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.payment.common.constant.CommonConstants;
import com.payment.common.util.BusinessIdGenerator;
import com.payment.engine.domain.PaymentStatus;
import com.payment.engine.entity.PaymentDetail;
import com.payment.engine.entity.PaymentMain;
import com.payment.engine.mapper.PaymentDetailMapper;
import com.payment.engine.mapper.PaymentMainMapper;
import com.payment.engine.mapper.TradeOrderLiteMapper;
import com.payment.engine.entity.TradeOrderLite;
import com.payment.engine.service.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentMainMapper paymentMainMapper;
    @Resource
    private PaymentDetailMapper paymentDetailMapper;
    @Resource
    private BusinessIdGenerator businessIdGenerator;
    @Resource
    private TradeOrderLiteMapper tradeOrderLiteMapper;

    @Override
    public String accept(String tradeOrderNo, String paymentMethod) {
        // 查询交易订单，获取金额币种
        TradeOrderLite order = tradeOrderLiteMapper.selectOne(new LambdaQueryWrapper<TradeOrderLite>()
                .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo));
        // 幂等：如果已有主单则复用
        PaymentMain main = paymentMainMapper.selectOne(new LambdaQueryWrapper<PaymentMain>()
                .eq(PaymentMain::getTradeOrderNo, tradeOrderNo));
        if (main == null) {
            main = new PaymentMain();
            main.setPaymentMainNo(businessIdGenerator.generatePaymentNo());
            main.setTradeOrderNo(tradeOrderNo);
            main.setAmount(order != null && order.getAmount() != null ? order.getAmount() : new BigDecimal("0.00"));
            main.setCurrency(order != null && order.getCurrency() != null ? order.getCurrency() : CommonConstants.CURRENCY_CNY);
            main.setStatus(PaymentStatus.INIT.getCode());
            main.setCreateTime(LocalDateTime.now());
            main.setUpdateTime(LocalDateTime.now());
            paymentMainMapper.insert(main);
        }

        PaymentDetail detail = new PaymentDetail();
        detail.setPaymentDetailNo(businessIdGenerator.generatePaymentNo());
        detail.setPaymentMainNo(main.getPaymentMainNo());
        detail.setPaymentMethod(paymentMethod);
        detail.setAmount(main.getAmount());
        detail.setCurrency(main.getCurrency());
        detail.setStatus(PaymentStatus.PAYING.getCode());
        detail.setCreateTime(LocalDateTime.now());
        detail.setUpdateTime(LocalDateTime.now());
        paymentDetailMapper.insert(detail);

        paymentMainMapper.update(null, new LambdaUpdateWrapper<PaymentMain>()
                .eq(PaymentMain::getPaymentMainNo, main.getPaymentMainNo())
                .set(PaymentMain::getStatus, PaymentStatus.PAYING.getCode())
                .set(PaymentMain::getUpdateTime, LocalDateTime.now()));

        // 更新交易订单状态为支付中
        if (order != null) {
            tradeOrderLiteMapper.update(null, new LambdaUpdateWrapper<TradeOrderLite>()
                    .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo)
                    .set(TradeOrderLite::getStatus, CommonConstants.TRADE_STATUS_PAYING));
        }

        return main.getPaymentMainNo();
    }

    @Override
    public String result(String tradeOrderNo) {
        PaymentMain main = paymentMainMapper.selectOne(new LambdaQueryWrapper<PaymentMain>()
                .eq(PaymentMain::getTradeOrderNo, tradeOrderNo));
        return main == null ? null : main.getStatus();
    }
}


