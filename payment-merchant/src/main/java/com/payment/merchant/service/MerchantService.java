package com.payment.merchant.service;

import com.payment.merchant.dto.MerchantRegisterRequest;
import com.payment.merchant.dto.MerchantResponse;
import com.payment.merchant.dto.MerchantUpdateRequest;

/**
 * 商户服务接口
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
public interface MerchantService {

    /**
     * 注册商户
     *
     * @param request 注册请求
     * @return 商户信息
     */
    MerchantResponse registerMerchant(MerchantRegisterRequest request);

    /**
     * 根据商户ID获取商户信息
     *
     * @param merchantId 商户ID
     * @return 商户信息
     */
    MerchantResponse getMerchantById(String merchantId);

    /**
     * 根据商户编码获取商户信息
     *
     * @param merchantCode 商户编码
     * @return 商户信息
     */
    MerchantResponse getMerchantByCode(String merchantCode);

    /**
     * 更新商户信息
     *
     * @param merchantId 商户ID
     * @param request 更新请求
     * @return 更新后的商户信息
     */
    MerchantResponse updateMerchant(String merchantId, MerchantUpdateRequest request);

    /**
     * 删除商户
     *
     * @param merchantId 商户ID
     */
    void deleteMerchant(String merchantId);

    /**
     * 激活商户
     *
     * @param merchantId 商户ID
     */
    void activateMerchant(String merchantId);

    /**
     * 停用商户
     *
     * @param merchantId 商户ID
     */
    void deactivateMerchant(String merchantId);
}
