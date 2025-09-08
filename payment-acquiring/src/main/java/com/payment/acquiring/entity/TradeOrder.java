package com.payment.acquiring.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_trade_order")
public class TradeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("trade_order_no")
    private String tradeOrderNo;

    @TableField("merchant_id")
    private String merchantId;

    @TableField("merchant_order_no")
    private String merchantOrderNo;

    @TableField("user_id")
    private String userId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("currency")
    private String currency;

    @TableField("subject")
    private String subject;

    @TableField("body")
    private String body;

    @TableField("status")
    private String status;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("notify_url")
    private String notifyUrl;

    @TableField("return_url")
    private String returnUrl;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}



