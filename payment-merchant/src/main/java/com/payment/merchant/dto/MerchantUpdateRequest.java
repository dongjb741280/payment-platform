package com.payment.merchant.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * 商户更新请求DTO
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
public class MerchantUpdateRequest {

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人手机
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    @Email(message = "邮箱格式不正确")
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
}
