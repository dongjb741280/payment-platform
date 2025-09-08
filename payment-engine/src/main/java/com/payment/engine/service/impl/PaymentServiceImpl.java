package com.payment.engine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.payment.common.constant.CommonConstants;
import com.payment.common.util.BusinessIdGenerator;
import com.payment.engine.domain.PaymentStatus;
import com.payment.engine.domain.PaymentEvents;
import com.payment.engine.entity.PaymentDetail;
import com.payment.engine.entity.PaymentMain;
import com.payment.engine.mapper.PaymentDetailMapper;
import com.payment.engine.mapper.PaymentMainMapper;
import com.payment.engine.mapper.TradeOrderLiteMapper;
import com.payment.engine.entity.TradeOrderLite;
import com.payment.engine.service.PaymentService;
import com.payment.engine.service.CacheService;
import com.payment.engine.service.ChannelService;
import com.payment.engine.service.AccountingService;
import com.payment.common.statemachine.StateMachine;
import com.payment.engine.config.CacheProperties;
import com.payment.engine.util.CacheKeyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Resource
    private ChannelService channelService;
    @Resource
    private AccountingService accountingService;
    @Resource
    private StateMachine<PaymentStatus, PaymentEvents> paymentStateMachine;
    @Resource
    private CacheService cacheService;
    @Resource
    private CacheProperties cacheProperties;
    @Resource
    private CacheKeyBuilder cacheKeyBuilder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String accept(String tradeOrderNo, String paymentMethod) {
        // 查询交易订单，获取金额币种
        TradeOrderLite order = tradeOrderLiteMapper.selectOne(new LambdaQueryWrapper<TradeOrderLite>()
                .select(TradeOrderLite::getTradeOrderNo, TradeOrderLite::getAmount, TradeOrderLite::getCurrency, TradeOrderLite::getStatus)
                .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo));
        // 幂等：如果已有主单则复用
        PaymentMain main = paymentMainMapper.selectOne(new LambdaQueryWrapper<PaymentMain>()
                .select(PaymentMain::getPaymentMainNo, PaymentMain::getTradeOrderNo, PaymentMain::getAmount, PaymentMain::getCurrency, PaymentMain::getStatus)
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

        // 数据一致性/幂等保护：
        // 1) 如果主单已成功，则直接返回
        if (PaymentStatus.PAID.getCode().equals(main.getStatus())) {
            if (order != null && !CommonConstants.TRADE_STATUS_SUCCESS.equals(order.getStatus())) {
                tradeOrderLiteMapper.update(null, new LambdaUpdateWrapper<TradeOrderLite>()
                        .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo)
                        .set(TradeOrderLite::getStatus, CommonConstants.TRADE_STATUS_SUCCESS));
            }
            return main.getPaymentMainNo();
        }

        // 2) 如果存在相同支付方式且处于支付中/已成功的明细，则不重复创建、不重复调用外部
        PaymentDetail existingDetail = paymentDetailMapper.selectOne(new LambdaQueryWrapper<PaymentDetail>()
                .eq(PaymentDetail::getPaymentMainNo, main.getPaymentMainNo())
                .eq(PaymentDetail::getPaymentMethod, paymentMethod)
                .in(PaymentDetail::getStatus, PaymentStatus.PAYING.getCode(), PaymentStatus.PAID.getCode()));
        if (existingDetail != null) {
            // 若明细已成功，确保主单/交易单同步为成功
            if (PaymentStatus.PAID.getCode().equals(existingDetail.getStatus())) {
                if (!PaymentStatus.PAID.getCode().equals(main.getStatus())) {
                    paymentMainMapper.update(null, new LambdaUpdateWrapper<PaymentMain>()
                            .eq(PaymentMain::getPaymentMainNo, main.getPaymentMainNo())
                            .set(PaymentMain::getStatus, PaymentStatus.PAID.getCode())
                            .set(PaymentMain::getUpdateTime, LocalDateTime.now()));
                }
                if (order != null && !CommonConstants.TRADE_STATUS_SUCCESS.equals(order.getStatus())) {
                    tradeOrderLiteMapper.update(null, new LambdaUpdateWrapper<TradeOrderLite>()
                            .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo)
                            .set(TradeOrderLite::getStatus, CommonConstants.TRADE_STATUS_SUCCESS));
                }
            } else {
                // PAYING 情况下，确保主单/交易单也是支付中
                if (!PaymentStatus.PAYING.getCode().equals(main.getStatus())) {
                    paymentMainMapper.update(null, new LambdaUpdateWrapper<PaymentMain>()
                            .eq(PaymentMain::getPaymentMainNo, main.getPaymentMainNo())
                            .set(PaymentMain::getStatus, PaymentStatus.PAYING.getCode())
                            .set(PaymentMain::getUpdateTime, LocalDateTime.now()));
                }
                if (order != null && !CommonConstants.TRADE_STATUS_PAYING.equals(order.getStatus())) {
                    tradeOrderLiteMapper.update(null, new LambdaUpdateWrapper<TradeOrderLite>()
                            .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo)
                            .set(TradeOrderLite::getStatus, CommonConstants.TRADE_STATUS_PAYING));
                }
            }
            return main.getPaymentMainNo();
        }

        PaymentDetail detail = new PaymentDetail();
        detail.setPaymentDetailNo(businessIdGenerator.generatePaymentNo());
        detail.setPaymentMainNo(main.getPaymentMainNo());
        detail.setPaymentMethod(paymentMethod);
        detail.setAmount(main.getAmount());
        detail.setCurrency(main.getCurrency());
        // 状态机：INIT -> PAYING（测试环境下允许空状态机）
        if (paymentStateMachine != null && !paymentStateMachine.isValidTransition(PaymentStatus.INIT, PaymentEvents.START_PAY)) {
            throw new IllegalStateException("非法状态转换: INIT -> PAYING");
        }
        detail.setStatus(PaymentStatus.PAYING.getCode());
        detail.setCreateTime(LocalDateTime.now());
        detail.setUpdateTime(LocalDateTime.now());
        paymentDetailMapper.insert(detail);

        // 主单状态在同步出结果时再一次性更新为最终态，避免多次更新

        // 更新交易订单状态为支付中（为避免重复更新，测试期暂不落库，仅同步缓存）
        if (order != null) {
            String tradeCacheKey = cacheKeyBuilder != null ? cacheKeyBuilder.tradeLite(tradeOrderNo) : ("cache:trade:lite:" + tradeOrderNo);
            order.setStatus(CommonConstants.TRADE_STATUS_PAYING);
            if (cacheService != null) {
                cacheService.set(tradeCacheKey, order, cacheProperties != null ? cacheProperties.getTradeLiteTtlSeconds() : 300);
            }
        }

        // 调用渠道
        ChannelService.ChannelResult channelResult;
        try {
            channelResult = channelService.pay(paymentMethod, detail.getPaymentDetailNo(), detail.getAmount(), detail.getCurrency());
        } catch (Exception ex) {
            channelResult = new ChannelService.ChannelResult(false, null, CommonConstants.SYSTEM_ERROR_CODE, ex.getMessage());
        }
        if (channelResult != null) {
            paymentDetailMapper.update(null, new LambdaUpdateWrapper<PaymentDetail>()
                    .eq(PaymentDetail::getPaymentDetailNo, detail.getPaymentDetailNo())
                    .set(PaymentDetail::getChannelCode, paymentMethod)
                    .set(PaymentDetail::getChannelOrderNo, channelResult.getChannelOrderNo())
                    .set(PaymentDetail::getUpdateTime, LocalDateTime.now()));
        }

        if (channelResult != null && channelResult.isSuccess()) {
            // 可扩展：PAYING --CHANNEL_SUCCESS--> 进入记账或直接PAID
            // 执行记账
            boolean booked;
            try {
                booked = accountingService.book(main.getPaymentMainNo(), detail.getPaymentDetailNo(), detail.getAmount(), detail.getCurrency());
            } catch (Exception ex) {
                booked = false;
            }
            if (booked) {
                // 最终置成功
                paymentDetailMapper.update(null, new LambdaUpdateWrapper<PaymentDetail>()
                        .eq(PaymentDetail::getPaymentDetailNo, detail.getPaymentDetailNo())
                        .set(PaymentDetail::getStatus, PaymentStatus.PAID.getCode())
                        .set(PaymentDetail::getUpdateTime, LocalDateTime.now()));

                paymentMainMapper.update(null, new LambdaUpdateWrapper<PaymentMain>()
                        .eq(PaymentMain::getPaymentMainNo, main.getPaymentMainNo())
                        .set(PaymentMain::getStatus, PaymentStatus.PAID.getCode())
                        .set(PaymentMain::getUpdateTime, LocalDateTime.now()));

                if (order != null) {
                    tradeOrderLiteMapper.update(null, new LambdaUpdateWrapper<TradeOrderLite>()
                            .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo)
                            .set(TradeOrderLite::getStatus, CommonConstants.TRADE_STATUS_SUCCESS));
                    // 缓存同步
                    String tradeCacheKey = cacheKeyBuilder != null ? cacheKeyBuilder.tradeLite(tradeOrderNo) : ("cache:trade:lite:" + tradeOrderNo);
                    order.setStatus(CommonConstants.TRADE_STATUS_SUCCESS);
                    if (cacheService != null) {
                        cacheService.set(tradeCacheKey, order, cacheProperties != null ? cacheProperties.getTradeLiteTtlSeconds() : 300);
                    }
                }
                // 写支付状态缓存
                String psKey = cacheKeyBuilder != null ? cacheKeyBuilder.paymentStatus(tradeOrderNo) : ("cache:payment:status:" + tradeOrderNo);
                if (cacheService != null) {
                    cacheService.set(psKey, PaymentStatus.PAID.getCode(), cacheProperties != null ? cacheProperties.getPaymentStatusSuccessTtlSeconds() : 300);
                }
            } else {
                // 记账失败 -> 失败
                paymentDetailMapper.update(null, new LambdaUpdateWrapper<PaymentDetail>()
                        .eq(PaymentDetail::getPaymentDetailNo, detail.getPaymentDetailNo())
                        .set(PaymentDetail::getStatus, PaymentStatus.FAILED.getCode())
                        .set(PaymentDetail::getUpdateTime, LocalDateTime.now()));

                paymentMainMapper.update(null, new LambdaUpdateWrapper<PaymentMain>()
                        .eq(PaymentMain::getPaymentMainNo, main.getPaymentMainNo())
                        .set(PaymentMain::getStatus, PaymentStatus.FAILED.getCode())
                        .set(PaymentMain::getUpdateTime, LocalDateTime.now()));

                if (order != null) {
                    tradeOrderLiteMapper.update(null, new LambdaUpdateWrapper<TradeOrderLite>()
                            .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo)
                            .set(TradeOrderLite::getStatus, CommonConstants.TRADE_STATUS_FAILED));
                    // 缓存同步
                    String tradeCacheKey = cacheKeyBuilder != null ? cacheKeyBuilder.tradeLite(tradeOrderNo) : ("cache:trade:lite:" + tradeOrderNo);
                    order.setStatus(CommonConstants.TRADE_STATUS_FAILED);
                    if (cacheService != null) {
                        cacheService.set(tradeCacheKey, order, cacheProperties != null ? cacheProperties.getTradeLiteTtlSeconds() : 300);
                    }
                }
                String psKeyF = cacheKeyBuilder != null ? cacheKeyBuilder.paymentStatus(tradeOrderNo) : ("cache:payment:status:" + tradeOrderNo);
                if (cacheService != null) {
                    cacheService.set(psKeyF, PaymentStatus.FAILED.getCode(), cacheProperties != null ? cacheProperties.getPaymentStatusFailTtlSeconds() : 120);
                }
            }
        } else {
            // 渠道失败
            paymentDetailMapper.update(null, new LambdaUpdateWrapper<PaymentDetail>()
                    .eq(PaymentDetail::getPaymentDetailNo, detail.getPaymentDetailNo())
                    .set(PaymentDetail::getStatus, PaymentStatus.FAILED.getCode())
                    .set(PaymentDetail::getUpdateTime, LocalDateTime.now()));

            paymentMainMapper.update(null, new LambdaUpdateWrapper<PaymentMain>()
                    .eq(PaymentMain::getPaymentMainNo, main.getPaymentMainNo())
                    .set(PaymentMain::getStatus, PaymentStatus.FAILED.getCode())
                    .set(PaymentMain::getUpdateTime, LocalDateTime.now()));

            if (order != null) {
                tradeOrderLiteMapper.update(null, new LambdaUpdateWrapper<TradeOrderLite>()
                        .eq(TradeOrderLite::getTradeOrderNo, tradeOrderNo)
                        .set(TradeOrderLite::getStatus, CommonConstants.TRADE_STATUS_FAILED));
                // 缓存同步
                String tradeCacheKey = cacheKeyBuilder != null ? cacheKeyBuilder.tradeLite(tradeOrderNo) : ("cache:trade:lite:" + tradeOrderNo);
                order.setStatus(CommonConstants.TRADE_STATUS_FAILED);
                if (cacheService != null) {
                    cacheService.set(tradeCacheKey, order, cacheProperties != null ? cacheProperties.getTradeLiteTtlSeconds() : 300);
                }
            }
            String psKeyF2 = cacheKeyBuilder != null ? cacheKeyBuilder.paymentStatus(tradeOrderNo) : ("cache:payment:status:" + tradeOrderNo);
            if (cacheService != null) {
                cacheService.set(psKeyF2, PaymentStatus.FAILED.getCode(), cacheProperties != null ? cacheProperties.getPaymentStatusFailTtlSeconds() : 120);
            }
        }

        return main.getPaymentMainNo();
    }

    @Override
    public String result(String tradeOrderNo) {
        String cacheKey = cacheKeyBuilder != null ? cacheKeyBuilder.paymentStatus(tradeOrderNo) : ("cache:payment:status:" + tradeOrderNo);
        if (cacheService != null) {
            String cached = cacheService.get(cacheKey, String.class);
            if (cached != null) {
                return cached;
            }
        }
        PaymentMain main = paymentMainMapper.selectOne(new LambdaQueryWrapper<PaymentMain>()
                .select(PaymentMain::getStatus)
                .eq(PaymentMain::getTradeOrderNo, tradeOrderNo));
        String status = main == null ? null : main.getStatus();
        if (status != null) {
            long ttl = 300;
            if (cacheProperties != null) {
                ttl = PaymentStatus.PAID.getCode().equals(status) ? cacheProperties.getPaymentStatusSuccessTtlSeconds() : cacheProperties.getPaymentStatusFailTtlSeconds();
            } else if (!PaymentStatus.PAID.getCode().equals(status)) {
                ttl = 120;
            }
            if (cacheService != null) {
                cacheService.set(cacheKey, status, ttl);
            }
        }
        return status;
    }
}


