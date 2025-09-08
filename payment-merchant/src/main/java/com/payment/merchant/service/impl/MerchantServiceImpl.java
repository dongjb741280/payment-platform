package com.payment.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.payment.common.constant.CommonConstants;
import com.payment.common.exception.BusinessException;
import com.payment.common.util.BusinessIdGenerator;
import com.payment.merchant.dto.MerchantRegisterRequest;
import com.payment.merchant.dto.MerchantResponse;
import com.payment.merchant.dto.MerchantUpdateRequest;
import com.payment.merchant.entity.Merchant;
import com.payment.merchant.mapper.MerchantMapper;
import com.payment.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import com.payment.common.constant.ErrorCodes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 商户服务实现类
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper merchantMapper;
    private final BusinessIdGenerator businessIdGenerator;
    private final MessageSource messageSource;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantResponse registerMerchant(MerchantRegisterRequest request) {
        log.info("开始注册商户，商户名称：{}", request.getMerchantName());

        // 检查商户编码是否已存在
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getMerchantCode, request.getMerchantCode());
        Merchant existingMerchant = merchantMapper.selectOne(queryWrapper);
        if (existingMerchant != null) {
            throw new BusinessException(ErrorCodes.MERCHANT_CODE_EXISTS, messageSource.getMessage("merchant.code.exists", null, LocaleContextHolder.getLocale()));
        }

        // 创建商户实体
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(request, merchant);
        
        // 生成商户ID（32位业务ID）
        merchant.setMerchantId(businessIdGenerator.generateMerchantId());
        
        // 生成密钥对（简化实现，实际应该使用专业的密钥生成工具）
        merchant.setPublicKey(generatePublicKey());
        merchant.setPrivateKey(generatePrivateKey());
        
        // 设置初始状态
        merchant.setStatus(CommonConstants.STATUS_INACTIVE);
        merchant.setCreateTime(LocalDateTime.now());
        merchant.setUpdateTime(LocalDateTime.now());
        merchant.setDeleted(false);

        // 保存商户信息
        int result = merchantMapper.insert(merchant);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.MERCHANT_REGISTER_FAILED, messageSource.getMessage("merchant.register.failed", null, LocaleContextHolder.getLocale()));
        }

        log.info("商户注册成功，商户ID：{}", merchant.getMerchantId());

        // 转换为响应DTO
        return convertToResponse(merchant);
    }

    @Override
    public MerchantResponse getMerchantById(String merchantId) {
        log.info("根据商户ID获取商户信息，商户ID：{}", merchantId);

        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getMerchantId, merchantId)
                   .eq(Merchant::getDeleted, false);
        
        Merchant merchant = merchantMapper.selectOne(queryWrapper);
        if (merchant == null) {
            throw new BusinessException(ErrorCodes.MERCHANT_NOT_FOUND, messageSource.getMessage("merchant.not.exist", null, LocaleContextHolder.getLocale()));
        }

        return convertToResponse(merchant);
    }

    @Override
    public MerchantResponse getMerchantByCode(String merchantCode) {
        log.info("根据商户编码获取商户信息，商户编码：{}", merchantCode);

        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getMerchantCode, merchantCode)
                   .eq(Merchant::getDeleted, false);
        
        Merchant merchant = merchantMapper.selectOne(queryWrapper);
        if (merchant == null) {
            throw new BusinessException(ErrorCodes.MERCHANT_NOT_FOUND, messageSource.getMessage("merchant.not.exist", null, LocaleContextHolder.getLocale()));
        }

        return convertToResponse(merchant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantResponse updateMerchant(String merchantId, MerchantUpdateRequest request) {
        log.info("更新商户信息，商户ID：{}", merchantId);

        // 检查商户是否存在
        Merchant existingMerchant = getMerchantEntityById(merchantId);
        
        // 更新商户信息
        BeanUtils.copyProperties(request, existingMerchant, "merchantId", "merchantCode", "createTime");
        existingMerchant.setUpdateTime(LocalDateTime.now());

        int result = merchantMapper.updateById(existingMerchant);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.MERCHANT_UPDATE_FAILED, messageSource.getMessage("merchant.update.failed", null, LocaleContextHolder.getLocale()));
        }

        log.info("商户信息更新成功，商户ID：{}", merchantId);

        return convertToResponse(existingMerchant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMerchant(String merchantId) {
        log.info("删除商户，商户ID：{}", merchantId);

        // 检查商户是否存在
        Merchant existingMerchant = getMerchantEntityById(merchantId);
        
        // 软删除
        existingMerchant.setDeleted(true);
        existingMerchant.setUpdateTime(LocalDateTime.now());

        int result = merchantMapper.updateById(existingMerchant);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.MERCHANT_DELETE_FAILED, messageSource.getMessage("merchant.delete.failed", null, LocaleContextHolder.getLocale()));
        }

        log.info("商户删除成功，商户ID：{}", merchantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateMerchant(String merchantId) {
        log.info("激活商户，商户ID：{}", merchantId);

        Merchant existingMerchant = getMerchantEntityById(merchantId);
        existingMerchant.setStatus(CommonConstants.STATUS_ACTIVE);
        existingMerchant.setUpdateTime(LocalDateTime.now());

        int result = merchantMapper.updateById(existingMerchant);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.MERCHANT_ACTIVATE_FAILED, messageSource.getMessage("merchant.activate.failed", null, LocaleContextHolder.getLocale()));
        }

        log.info("商户激活成功，商户ID：{}", merchantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deactivateMerchant(String merchantId) {
        log.info("停用商户，商户ID：{}", merchantId);

        Merchant existingMerchant = getMerchantEntityById(merchantId);
        existingMerchant.setStatus(CommonConstants.STATUS_INACTIVE);
        existingMerchant.setUpdateTime(LocalDateTime.now());

        int result = merchantMapper.updateById(existingMerchant);
        if (result <= 0) {
            throw new BusinessException(ErrorCodes.MERCHANT_DEACTIVATE_FAILED, messageSource.getMessage("merchant.deactivate.failed", null, LocaleContextHolder.getLocale()));
        }

        log.info("商户停用成功，商户ID：{}", merchantId);
    }

    /**
     * 根据商户ID获取商户实体
     */
    private Merchant getMerchantEntityById(String merchantId) {
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getMerchantId, merchantId)
                   .eq(Merchant::getDeleted, false);
        
        Merchant merchant = merchantMapper.selectOne(queryWrapper);
        if (merchant == null) {
            throw new BusinessException(ErrorCodes.MERCHANT_NOT_FOUND, messageSource.getMessage("merchant.not.exist", null, LocaleContextHolder.getLocale()));
        }
        return merchant;
    }

    /**
     * 转换为响应DTO
     */
    private MerchantResponse convertToResponse(Merchant merchant) {
        MerchantResponse response = new MerchantResponse();
        BeanUtils.copyProperties(merchant, response);
        return response;
    }

    /**
     * 生成公钥（简化实现）
     */
    private String generatePublicKey() {
        return "PUBLIC_KEY_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成私钥（简化实现）
     */
    private String generatePrivateKey() {
        return "PRIVATE_KEY_" + UUID.randomUUID().toString().replace("-", "");
    }
}
