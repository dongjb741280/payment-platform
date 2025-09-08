package com.payment.common.statemachine;

/**
 * 状态基类接口
 *
 * @author Payment Platform Team
 * @since 1.0.0
 */
public interface BaseStatus {
    /**
     * 获取状态码
     */
    String getCode();

    /**
     * 获取状态描述
     */
    String getDescription();
}
