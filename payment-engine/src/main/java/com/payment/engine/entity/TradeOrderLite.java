package com.payment.engine.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_trade_order")
public class TradeOrderLite {
    @TableField("trade_order_no")
    private String tradeOrderNo;
    @TableField("amount")
    private BigDecimal amount;
    @TableField("currency")
    private String currency;
    @TableField("status")
    private String status;
}


