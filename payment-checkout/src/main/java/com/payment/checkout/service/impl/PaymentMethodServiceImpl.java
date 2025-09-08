package com.payment.checkout.service.impl;

import com.payment.checkout.dto.PaymentMethodDTO;
import com.payment.checkout.dto.FormFieldDTO;
import com.payment.checkout.client.AcquiringClient;
import com.payment.checkout.service.RiskService;
import com.payment.checkout.service.PaymentMethodService;
import com.payment.common.constant.CommonConstants;
import org.springframework.stereotype.Service;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    @javax.annotation.Resource
    private RiskService riskService;

    @javax.annotation.Resource
    private AcquiringClient acquiringClient;

    private MessageSource messageSource;

    // 为测试构造兼容保留
    public PaymentMethodServiceImpl() {}

    public PaymentMethodServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    @Override
    public List<PaymentMethodDTO> consult(String tradeOrderNo) {
        // 简化演示：静态方法列表 + 元信息 + 排序
        List<PaymentMethodDTO> list = new ArrayList<>();
        PaymentMethodDTO balance = new PaymentMethodDTO(CommonConstants.PAYMENT_METHOD_BALANCE, getMsg("payment.method.balance", "余额支付"), 10, true);
        balance.setIcon("icon-balance");
        list.add(balance);

        PaymentMethodDTO wechat = new PaymentMethodDTO(CommonConstants.PAYMENT_METHOD_WECHAT, getMsg("payment.method.wechat", "微信支付"), 20, true);
        wechat.setIcon("icon-wechat");
        list.add(wechat);

        PaymentMethodDTO alipay = new PaymentMethodDTO(CommonConstants.PAYMENT_METHOD_ALIPAY, getMsg("payment.method.alipay", "支付宝"), 30, true);
        alipay.setIcon("icon-alipay");
        list.add(alipay);

        PaymentMethodDTO unionpay = new PaymentMethodDTO(CommonConstants.PAYMENT_METHOD_UNIONPAY, getMsg("payment.method.unionpay", "银联"), 40, false);
        unionpay.setIcon("icon-unionpay");
        unionpay.setDisabledReason("渠道维护中");
        list.add(unionpay);

        PaymentMethodDTO bankCard = new PaymentMethodDTO(CommonConstants.PAYMENT_METHOD_BANK_CARD, "银行卡", 50, true);
        bankCard.setIcon("icon-bankcard");
        bankCard.setFields(java.util.List.of(
            new FormFieldDTO("cardNo", "卡号", "text", true, "请输入银行卡号"),
            new FormFieldDTO("holderName", "持卡人", "text", true, "请输入持卡人姓名"),
            new FormFieldDTO("idNo", "证件号", "text", true, "请输入证件号码"),
            new FormFieldDTO("mobile", "预留手机号", "text", true, "银行预留手机号")
        ));
        list.add(bankCard);

        PaymentMethodDTO qr = new PaymentMethodDTO(CommonConstants.PAYMENT_METHOD_QRCODE, "二维码支付", 60, true);
        qr.setIcon("icon-qrcode");
        list.add(qr);

        // 动态可用性：基于商户配置与风控
        String merchantId = null;
        if (acquiringClient != null) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> order = (java.util.Map<String, Object>) acquiringClient.getOrderByTradeOrderNo(tradeOrderNo);
            merchantId = order != null && order.get("merchantId") != null ? order.get("merchantId").toString() : null;
        }

        for (PaymentMethodDTO pm : list) {
            boolean merchantAllowed = merchantAllowed(merchantId, pm.getCode());
            boolean riskAllowed = (riskService == null) || riskService.passBasicCheck(tradeOrderNo, pm.getCode());
            boolean available = merchantAllowed && riskAllowed && pm.isAvailable();
            pm.setAvailable(available);
            if (!available) {
                if (!merchantAllowed) {
                    pm.setDisabledReason("商户未开通该支付方式");
                } else if (!riskAllowed) {
                    pm.setDisabledReason("风控限制，暂不可用");
                }
            }
        }
        list.sort(Comparator.comparingInt(PaymentMethodDTO::getSort));
        return list;
    }

    private boolean merchantAllowed(String merchantId, String paymentMethodCode) {
        // 简化：无配置中心时默认全开；可替换为拉取商户配置判定
        return true;
    }

    private String getMsg(String code, String defaultStr) {
        if (messageSource == null) {
            return defaultStr;
        }
        try {
            return messageSource.getMessage(code, null, org.springframework.context.i18n.LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return defaultStr;
        }
    }
}


