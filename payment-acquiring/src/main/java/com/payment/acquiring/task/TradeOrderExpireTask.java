package com.payment.acquiring.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.payment.acquiring.domain.TradeOrderStatus;
import com.payment.acquiring.entity.TradeOrder;
import com.payment.acquiring.mapper.TradeOrderMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
public class TradeOrderExpireTask {

    @Resource
    private TradeOrderMapper tradeOrderMapper;

    // 每分钟执行一次，关闭已过期且未完成的订单
    @Scheduled(cron = "0 * * * * ?")
    public void expireOrders() {
        tradeOrderMapper.update(null, new LambdaUpdateWrapper<TradeOrder>()
                .in(TradeOrder::getStatus, TradeOrderStatus.INIT.getCode(), TradeOrderStatus.PAYING.getCode())
                .lt(TradeOrder::getExpireTime, LocalDateTime.now())
                .set(TradeOrder::getStatus, TradeOrderStatus.CLOSED.getCode())
                .set(TradeOrder::getUpdateTime, LocalDateTime.now()));
    }
}



