package com.payment.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商户实体类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_merchant")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商户ID
     */
    @TableField("merchant_id")
    private String merchantId;

    /**
     * 商户名称
     */
    @TableField("merchant_name")
    private String merchantName;

    /**
     * 商户编码
     */
    @TableField("merchant_code")
    private String merchantCode;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * 联系人手机
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    @TableField("contact_email")
    private String contactEmail;

    /**
     * 营业执照号
     */
    @TableField("business_license")
    private String businessLicense;

    /**
     * 法人姓名
     */
    @TableField("legal_person")
    private String legalPerson;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 公钥
     */
    @TableField("public_key")
    private String publicKey;

    /**
     * 私钥
     */
    @TableField("private_key")
    private String privateKey;

    /**
     * 状态
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
