package com.payment.account.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户实体类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_account")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账户ID
     */
    @TableField("account_id")
    private String accountId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 商户ID
     */
    @TableField("merchant_id")
    private String merchantId;

    /**
     * 账户类型
     */
    @TableField("account_type")
    private String accountType;

    /**
     * 币种
     */
    @TableField("currency")
    private String currency;

    /**
     * 账户状态
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
}
