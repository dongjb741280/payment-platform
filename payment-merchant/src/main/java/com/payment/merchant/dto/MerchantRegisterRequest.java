package com.payment.merchant.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 商户注册请求DTO
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Data
public class MerchantRegisterRequest {

    /**
     * 商户名称
     */
    @NotBlank(message = "商户名称不能为空")
    private String merchantName;

    /**
     * 商户编码
     */
    @NotBlank(message = "商户编码不能为空")
    @Pattern(regexp = "^[A-Z0-9_]{6,20}$", message = "商户编码格式不正确")
    private String merchantCode;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    /**
     * 联系人手机
     */
    @NotBlank(message = "联系人手机不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    @NotBlank(message = "联系人邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String contactEmail;

    /**
     * 营业执照号
     */
    @NotBlank(message = "营业执照号不能为空")
    private String businessLicense;

    /**
     * 法人姓名
     */
    @NotBlank(message = "法人姓名不能为空")
    private String legalPerson;

    /**
     * 地址
     */
    @NotBlank(message = "地址不能为空")
    private String address;
}
