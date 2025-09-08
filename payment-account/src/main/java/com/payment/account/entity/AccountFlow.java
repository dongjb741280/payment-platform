package com.payment.account.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户流水实体类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_account_flow")
public class AccountFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流水号
     */
    @TableField("flow_no")
    private String flowNo;

    /**
     * 账户ID
     */
    @TableField("account_id")
    private String accountId;

    /**
     * 交易类型
     */
    @TableField("transaction_type")
    private String transactionType;

    /**
     * 交易金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 交易前余额
     */
    @TableField("before_balance")
    private BigDecimal beforeBalance;

    /**
     * 交易后余额
     */
    @TableField("after_balance")
    private BigDecimal afterBalance;

    /**
     * 币种
     */
    @TableField("currency")
    private String currency;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
