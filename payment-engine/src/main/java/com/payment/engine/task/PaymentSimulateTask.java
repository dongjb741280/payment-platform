package com.payment.engine.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.payment.common.constant.CommonConstants;
import com.payment.engine.domain.PaymentStatus;
import com.payment.engine.entity.PaymentDetail;
import com.payment.engine.entity.PaymentMain;
import com.payment.engine.entity.TradeOrderLite;
import com.payment.engine.mapper.PaymentDetailMapper;
import com.payment.engine.mapper.PaymentMainMapper;
import com.payment.engine.mapper.TradeOrderLiteMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
public class PaymentSimulateTask {

    @Resource
    private PaymentMainMapper paymentMainMapper;
    @Resource
    private PaymentDetailMapper paymentDetailMapper;
    @Resource
    private TradeOrderLiteMapper tradeOrderLiteMapper;

    // 每2分钟模拟将支付中订单置为成功
    @Scheduled(cron = "0 */2 * * * ?")
    public void simulatePaid() {
        paymentMainMapper.update(null, new LambdaUpdateWrapper<PaymentMain>()
                .eq(PaymentMain::getStatus, PaymentStatus.PAYING.getCode())
                .set(PaymentMain::getStatus, PaymentStatus.PAID.getCode())
                .set(PaymentMain::getUpdateTime, LocalDateTime.now()));

        paymentDetailMapper.update(null, new LambdaUpdateWrapper<PaymentDetail>()
                .eq(PaymentDetail::getStatus, PaymentStatus.PAYING.getCode())
                .set(PaymentDetail::getStatus, PaymentStatus.PAID.getCode())
                .set(PaymentDetail::getUpdateTime, LocalDateTime.now()));

        tradeOrderLiteMapper.update(null, new LambdaUpdateWrapper<TradeOrderLite>()
                .eq(TradeOrderLite::getStatus, CommonConstants.TRADE_STATUS_PAYING)
                .set(TradeOrderLite::getStatus, CommonConstants.TRADE_STATUS_SUCCESS));
    }
}


