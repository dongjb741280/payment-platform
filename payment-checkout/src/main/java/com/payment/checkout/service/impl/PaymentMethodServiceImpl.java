package com.payment.checkout.service.impl;

import com.payment.checkout.dto.PaymentMethodDTO;
import com.payment.checkout.service.PaymentMethodService;
import com.payment.common.constant.CommonConstants;
import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {
    
    private final MessageSource messageSource;
    @Override
    public List<PaymentMethodDTO> consult(String tradeOrderNo) {
        // 简化演示：静态方法列表 + 排序
        List<PaymentMethodDTO> list = new ArrayList<>();
        list.add(new PaymentMethodDTO(CommonConstants.PAYMENT_METHOD_BALANCE, messageSource.getMessage("payment.method.balance", null, LocaleContextHolder.getLocale()), 10, true));
        list.add(new PaymentMethodDTO(CommonConstants.PAYMENT_METHOD_WECHAT, messageSource.getMessage("payment.method.wechat", null, LocaleContextHolder.getLocale()), 20, true));
        list.add(new PaymentMethodDTO(CommonConstants.PAYMENT_METHOD_ALIPAY, messageSource.getMessage("payment.method.alipay", null, LocaleContextHolder.getLocale()), 30, true));
        list.add(new PaymentMethodDTO(CommonConstants.PAYMENT_METHOD_UNIONPAY, messageSource.getMessage("payment.method.unionpay", null, LocaleContextHolder.getLocale()), 40, false));
        list.sort(Comparator.comparingInt(PaymentMethodDTO::getSort));
        return list;
    }
}


