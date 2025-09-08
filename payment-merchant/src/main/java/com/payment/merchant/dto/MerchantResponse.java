package com.payment.merchant.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商户响应DTO
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
public class MerchantResponse {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人手机
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 营业执照号
     */
    private String businessLicense;

    /**
     * 法人姓名
     */
    private String legalPerson;

    /**
     * 地址
     */
    private String address;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
